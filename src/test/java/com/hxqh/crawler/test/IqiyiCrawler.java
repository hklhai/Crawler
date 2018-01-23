package com.hxqh.crawler.test;


import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.PersistFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
//import org.apache.commons.collections4.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/1/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IqiyiCrawler {

    @Resource
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    @Test
    public void persist() throws IOException, InterruptedException {
        // 1. 从数据库获取待爬取链接
        List<String> hrefList = new ArrayList<>();

        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findAll();
        for (CrawlerURL crawlerURL : crawlerURLS) {
            hrefList.add(crawlerURL.getUrl());
        }

//        List<List<String>> lists = ListUtils.partition(hrefList, Constants.PARTITION_NUM);

//        ExecutorService service = Executors.newFixedThreadPool(Constants.THREAD_NUM);
//
//        for (List<String> l : lists) {
//            service.execute(new PersistFilm(l, crawlerProblemRepository));
//        }
//        service.shutdown();
//
//        while (!service.isTerminated()) {
//        }



    }
}

