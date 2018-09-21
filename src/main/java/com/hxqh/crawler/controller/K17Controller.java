package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistLiterature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
import com.hxqh.crawler.service.SystemService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/17k")
public class K17Controller {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;


    /**
     * 爬取17K网络文学URL
     *
     * @return
     */
    @RequestMapping("/literatureUrl")
    public String literatureUrl() {


        return "crawler/notice";
    }


    /**
     * 爬取17K网络文学数据
     *
     * @return
     */
    @RequestMapping("/literatureData")
    public String literatureData() {

        List<CrawlerLiteratureURL> varietyURLList = crawlerLiteratureURLRepository.findAll();

        List<CrawlerLiteratureURL> urlList = varietyURLList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

        Integer partitionNUm = urlList.size() / Constants.THREAD_NUM_17K + 1;
        List<List<CrawlerLiteratureURL>> lists = ListUtils.partition(urlList, partitionNUm);

        // ExecutorService service = Executors.newFixedThreadPool(Constants.THREAD_NUM_17K);
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.THREAD_NUM_17K,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());


        for (List<CrawlerLiteratureURL> list : lists) {
            service.execute(new PersistLiterature(systemService, list));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }


        return "crawler/notice";
    }

}
