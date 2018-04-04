package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistDouBan;
import com.hxqh.crawler.model.CrawlerDoubanScore;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */

@Component
public class DouBanTimer {


    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;


    @Scheduled(cron = "0 0 12 * * ?")
    public void douBanForFilm() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
                // 1. 从数据库获取待爬取链接
                List<CrawlerURL> crawlerURLS = crawlerURLRepository.findFilm();
                List<CrawlerDoubanScore> doubanScoreList = crawlerDoubanSocreRepository.findAll();

                for (int i = 0; i < crawlerURLS.size(); i++) {
                    for (int j = 0; j < doubanScoreList.size(); j++) {
                        CrawlerURL crawlerURL = crawlerURLS.get(i);
                        CrawlerDoubanScore doubanScore = doubanScoreList.get(j);
                        // title 和 category
                        if (crawlerURL.getTitle().equals(doubanScore.getTitle())
                                && crawlerURL.getCategory().equals(doubanScore.getCategory())) {
                            crawlerURLS.remove(crawlerURL);
                        }

                    }
                }
                crawlerURLS = crawlerURLS.subList(0, 800);
                Integer partitionNUm = crawlerURLS.size() / Constants.DOUBAN_THREAD_NUM + 1;
                List<List<CrawlerURL>> lists = ListUtils.partition(crawlerURLS, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.DOUBAN_THREAD_NUM);

                for (List<CrawlerURL> l : lists) {
                    service.execute(new PersistDouBan(l, crawlerDoubanSocreRepository, "film"));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
