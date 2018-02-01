package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistJdBook;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Ocean lin on 2018/1/30.
 *
 * @author Ocean Lin
 */
@Component
public class JdTimer {

    private final static Integer JD_PAGE_NUM = 6;

    @Autowired
    private CrawlerBookURLRepository crawlerBookURLRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    /**
     * 1. 获取爬取列表前先将数据写入ES
     * 2. 清除所有mysql数据
     * 3. 进行爬取
     */
    // 每个星期日0点15分
    @Scheduled(cron = "0 15 0 ? * SUN")
    public void jdUrlList() {
// todo
//        /**
//         * 取爬取列表前先将数据写入ES
//         */
//        List<CrawlerBookURL> crawlerURLList = crawlerBookURLRepository.findBookUrl();
//        ResponseEntity responseEntity = systemService.add List(crawlerURLList);
//
//        /**
//         * 清除所有mysql数据
//         */
//        if (responseEntity.getStatusCodeValue() > 0) {
//            crawlerURLRepository.deleteIqiyiFilm();
//        }
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {

                // 根据其实页面，爬取页面获取Url对应关系
                Map<String, String> map = new HashMap<>(500);

                String startpage = "http://book.jd.com/booktop/0-0-0.html?category=3287-0-0-0-10001-1#comfort";
                try {
                    String outerHTML = CrawlerUtils.fetchHTMLContent(startpage, 6);
                    Document doc = Jsoup.parse(outerHTML);
                    Element div = doc.getElementsByClass("mc").get(0);
                    Elements aLabels = div.select("a");
                    Elements elements = div.select("dl").select("dt").select("a");
                    for (Element element : aLabels) {
                        map.put(element.attr("href"), element.text());
                    }
                    for (Element element : elements) {
                        map.put(element.attr("href"), element.text());
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /**
                 * 样式
                 * //book.jd.com/booktop/13627-0-0-0-10001-1.html#comfort:书法
                 * //book.jd.com/booktop/20007-0-0-0-10001-1.html#comfort:科技
                 *
                 * 根据url反向截取，获取前5页每页20条数据url
                 */
                for (Map.Entry<String, String> m : map.entrySet()) {
                    List<String> urlList = new ArrayList<>();

                    // 获取页面中所有url
                    String urlPageOne = m.getKey();
                    String category = m.getValue();

                    // 根据url样式生成前5页url
                    String categoryId = (urlPageOne.split("-"))[0].split("/")[4];

                    String prefix = "http://book.jd.com/booktop/0-0-0.html?category=" + categoryId + "-0-0-0-10001-";
                    String subfix = "#comfort";
                    for (int i = 1; i < JD_PAGE_NUM; i++) {
                        String url = prefix + i + subfix;
                        urlList.add(url);
                    }
                    // 爬取页面中所有的url对应的页面中每本书的信息
                    List<CrawlerBookURL> crawlerURLList = new ArrayList<>();
                    for (String url : urlList) {
                        try {
                            // 解析url获取其中的每本的信息
                            String outerHTML = CrawlerUtils.fetchHTMLContent(url, 6);
                            Document doc = Jsoup.parse(outerHTML);

                            Elements elements = doc.getElementsByClass("p-detail");
                            for (Element e : elements) {
                                Elements a = e.select("a");
                                CrawlerBookURL crawlerBookURL = new CrawlerBookURL(
                                        "http:" + a.attr("href"),
                                        a.attr("title"),
                                        DateUtils.getTodayDate(),
                                        category, "jd");
                                crawlerURLList.add(crawlerBookURL);
                            }
                            // 完成持久化
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    crawlerBookURLRepository.save(crawlerURLList);

                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //每天1点5分触发
    @Scheduled(cron = "0 5 1 * * ?")
    public void jdData() {

        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
                // 1. 从数据库获取待爬取链接
                List<CrawlerBookURL> crawlerBookURLList = crawlerBookURLRepository.findAll();
                List<List<CrawlerBookURL>> lists = ListUtils.partition(crawlerBookURLList, Constants.JD_PARTITION_NUM);

                ExecutorService service = Executors.newFixedThreadPool(Constants.JD_THREAD_NUM);
                for (List<CrawlerBookURL> list : lists) {
                    service.execute(new PersistJdBook(list, crawlerProblemRepository, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-jd", Constants.BOOK_JD_FILE_LOC);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
