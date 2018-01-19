package com.hxqh.crawler.test;


import com.hxqh.crawler.controller.PersistFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
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


    @Test
    public void persist() throws IOException, InterruptedException {
        // 1. 从数据库获取待爬取链接
        List<String> hrefList = new ArrayList<>();

        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findAll();
        for (CrawlerURL crawlerURL : crawlerURLS) {
            hrefList.add(crawlerURL.getUrl());
        }

        List<List<String>> lists = ListUtils.partition(hrefList, 222);

//        List<String> hrefList = Arrays.asList("http://www.iqiyi.com/v_19rr7pgf5g.html#vfrm=2-4-0-1");

        ExecutorService service = Executors.newFixedThreadPool(4);

        for (List<String> l : lists) {
            service.execute(new PersistFilm(l));
        }
        service.shutdown();

        while (!service.isTerminated()) {
        }

    }
}

