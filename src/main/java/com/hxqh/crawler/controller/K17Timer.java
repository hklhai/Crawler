package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistLiterature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/3/22.
 *
 * @author Ocean lin
 */
@Component
public class K17Timer {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;

    // 每月最后一日的上午10:15触发
    @Scheduled(cron = "0 15 10 14 * ?")
    public void k17Url() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {

                List<String> list = new ArrayList<>();

                // 爬取10页
                for (int i = 1; i <= 10; i++) {
                    String s = "http://all.17k.com/lib/book/2_0_0_0_0_0_0_0_";
                    list.add(s + i + ".html");
                }

                for (int i = 0; i < list.size(); i++) {
                    List<CrawlerLiteratureURL> crawlerLiteratureURLList = new ArrayList<>();
                    String url = list.get(i);
                    String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 5);
                    Document doc = Jsoup.parse(html);
                    Elements jts = doc.getElementsByClass("jt");
                    for (int j = 1; j < jts.size(); j++) {
                        System.out.println(jts.get(j).attr("href") + " " + jts.get(j).text());

                        CrawlerLiteratureURL literatureURL = new CrawlerLiteratureURL(jts.get(j).text(),
                                jts.get(j).attr("href"),
                                DateUtils.getTodayDate(),
                                "17k",
                                ""
                        );
                        crawlerLiteratureURLList.add(literatureURL);
                        // 持久化值ElasticSearch
                        systemService.saveLiterature(literatureURL);
                    }
                    // 持久化至MYSQL
                    crawlerLiteratureURLRepository.save(crawlerLiteratureURLList);
                }


            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 每天10点00分触发
    @Scheduled(cron = "0 0 10 * * ?")
    public void k17Data() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {

                List<CrawlerLiteratureURL> varietyURLList = crawlerLiteratureURLRepository.findAll();
                Integer partitionNUm = varietyURLList.size() / Constants.THREAD_NUM_17K + 1;
                List<List<CrawlerLiteratureURL>> lists = ListUtils.partition(varietyURLList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.THREAD_NUM_17K);

                for (List<CrawlerLiteratureURL> list : lists) {
                    service.execute(new PersistLiterature(systemService, list));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-literature-17k", Constants.FILE_LOC);
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