package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistJdBook;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;


/**
 * <p>
 * Created by Ocean lin on 2018/1/30.
 *
 * @author Ocean Lin
 */
@Component
public class JdTimer {
    private final static Integer JD_PAGE_NUM = 6;

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private CrawlerBookURLRepository crawlerBookURLRepository;

    /**
     * 1. 获取爬取列表前先将数据写入ES
     * 2. 清除所有mysql数据
     * 3. 进行爬取
     * <p>
     * 每月14日上午10:15触发
     */
    @Scheduled(cron = "0 15 10 14 * ?")
    public void jdUrlList() {

        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {

                /**
                 * 爬取数据
                 */
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
                } catch (Exception e) {
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
                                // 持久化至ElasticSearch
                                systemService.addBookURL(crawlerBookURL);

                                crawlerURLList.add(crawlerBookURL);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 完成持久化
                        crawlerService.persistBookUrl(crawlerURLList);
                        crawlerService.persistBookUrl(crawlerURLList);
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 每天0点10分触发
     */
    @Scheduled(cron = "0 18 0 * * ?")
    public void jdData() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK3)) {

                // 1. 从数据库获取待爬取链接
                List<CrawlerBookURL> crawlerBookURLList = crawlerBookURLRepository.findAll();

                List<CrawlerBookURL> urlList = crawlerBookURLList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                        -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

                Integer partitionNUm = urlList.size() / Constants.JD_THREAD_NUM + 1;
                List<List<CrawlerBookURL>> lists = ListUtils.partition(urlList, partitionNUm);

                ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.JD_THREAD_NUM,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

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
