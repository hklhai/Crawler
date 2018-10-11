package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistJdBook;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
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
     * 每月15号上午10:15触发
     */
    @Scheduled(cron = "0 15 10 15 * ?")
    public void jdUrlList() {


    }


    /**
     * 每天18点00分触发
     */
    @Scheduled(cron = "0 0 18 * * ?")
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
