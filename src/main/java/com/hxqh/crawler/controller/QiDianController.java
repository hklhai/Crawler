package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistLiterature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/qidian")
public class QiDianController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;


    /**
     * 爬取起点网络文学URL
     *
     * @return
     */
    @RequestMapping("/literatureUrl")
    public String literatureUrl() {

        List<String> hotList = new ArrayList<>();
        List<String> newList = new ArrayList<>();

        String hotUrl = "https://www.qidian.com/all?orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=";
        String newUrl = "https://www.qidian.com/all?orderId=5&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=";

        for (int i = 1; i <= 20; i++) {
            hotList.add(hotUrl + String.valueOf(i));
        }
        for (int i = 1; i <= 20; i++) {
            newList.add(newUrl + String.valueOf(i));
        }

        persistList(hotList, "hot");
        persistList(newList, "new");


        return "crawler/notice";
    }

    /**
     * 持久化爬取结果
     *
     * @param hotList url列表
     * @param sorted  排序规则
     */
    private void persistList(List<String> hotList, String sorted) {
        // 获取当前页面所有链接地址
        for (int i = 0; i < hotList.size(); i++) {
            List<CrawlerLiteratureURL> crawlerLiteratureURLList = new ArrayList<>();
            String s = hotList.get(i);
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(s, 3);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.getElementsByClass("book-mid-info");
            for (int j = 0; j < elements.size(); j++) {

                Element element = elements.get(j);
                String href = element.select("h4").get(0).select("a").get(0).attr("href");
                String title = element.select("h4").get(0).select("a").get(0).text();
                CrawlerLiteratureURL literatureURL = new CrawlerLiteratureURL();

                literatureURL.setUrl("http:" + href);
                literatureURL.setAddTime(DateUtils.getTodayDate());
                literatureURL.setPlatform("qidian");
                literatureURL.setTitle(title);
                literatureURL.setSorted(sorted);
                crawlerLiteratureURLList.add(literatureURL);
            }
            crawlerLiteratureURLRepository.save(crawlerLiteratureURLList);
        }
    }


    /**
     * 爬取起点网络文学数据
     *
     * @return
     */
    @RequestMapping("/literatureDataUrl")
    public String literatureDataUrl() {

        List<CrawlerLiteratureURL> varietyURLList = crawlerLiteratureURLRepository.findAll();
        Integer partitionNUm = varietyURLList.size() / Constants.QIDIAN_THREAD_NUM + 1;
        List<List<CrawlerLiteratureURL>> lists = ListUtils.partition(varietyURLList, partitionNUm);

        ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

        for (List<CrawlerLiteratureURL> list : lists) {
            service.execute(new PersistLiterature(systemService, list));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }

        // 2. 上传至HDFS
        try {
            HdfsUtils.persistToHDFS("-literature-qidian", Constants.FILE_LOC);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "crawler/notice";
    }

}
