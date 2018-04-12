package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistDouBan;
import com.hxqh.crawler.controller.thread.PersistDouBanBook;
import com.hxqh.crawler.controller.thread.PersistDouBanSoap;
import com.hxqh.crawler.model.VDouBanCrawlerBook;
import com.hxqh.crawler.model.VDouBanCrawlerFilm;
import com.hxqh.crawler.model.VDouBanCrawlerSoap;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.repository.VDouBanCrawlerBookRepository;
import com.hxqh.crawler.repository.VDouBanCrawlerFilmRepository;
import com.hxqh.crawler.repository.VDouBanCrawlerSoapRepository;
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
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;
    @Autowired
    private VDouBanCrawlerFilmRepository douBanCrawlerFilmRepository;
    @Autowired
    private VDouBanCrawlerSoapRepository crawlerSoapRepository;
    @Autowired
    private VDouBanCrawlerBookRepository douBanCrawlerBookRepository;


    @Scheduled(cron = "0 0 12 * * ?")
    public void douBanForFilm() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {

                List<VDouBanCrawlerFilm> douBanCrawlerFilmList = douBanCrawlerFilmRepository.findAll();
                douBanCrawlerFilmList = douBanCrawlerFilmList.subList(0, 200);

                Integer partitionNUm = douBanCrawlerFilmList.size() / Constants.DOUBAN_THREAD_NUM + 1;
                List<List<VDouBanCrawlerFilm>> lists = ListUtils.partition(douBanCrawlerFilmList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.DOUBAN_THREAD_NUM);

                for (List<VDouBanCrawlerFilm> l : lists) {
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


    @Scheduled(cron = "0 0 14 * * ?")
    public void douBanForSoap() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
                List<VDouBanCrawlerSoap> douBanCrawlerSoapList = crawlerSoapRepository.findAll();

                douBanCrawlerSoapList = douBanCrawlerSoapList.subList(0, 200);
                Integer partitionNUm = douBanCrawlerSoapList.size() / Constants.DOUBAN_THREAD_NUM + 1;
                List<List<VDouBanCrawlerSoap>> lists = ListUtils.partition(douBanCrawlerSoapList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.DOUBAN_THREAD_NUM);

                for (List<VDouBanCrawlerSoap> l : lists) {
                    service.execute(new PersistDouBanSoap(l, crawlerDoubanSocreRepository, "soap"));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 0 16 * * ?")
    public void douBanForBook() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
                List<VDouBanCrawlerBook> douBanCrawlerBookList = douBanCrawlerBookRepository.findAll();

                douBanCrawlerBookList = douBanCrawlerBookList.subList(0, 200);
                Integer partitionNUm = douBanCrawlerBookList.size() / Constants.DOUBAN_THREAD_NUM + 1;
                List<List<VDouBanCrawlerBook>> lists = ListUtils.partition(douBanCrawlerBookList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.DOUBAN_THREAD_NUM);

                for (List<VDouBanCrawlerBook> l : lists) {
                    service.execute(new PersistDouBanBook(l, crawlerDoubanSocreRepository, "soap"));
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
