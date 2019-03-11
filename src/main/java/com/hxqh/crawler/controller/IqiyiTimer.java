package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerSoapURLRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.repository.CrawlerVarietyURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import static com.hxqh.crawler.common.Constants.PAGE;
import static com.hxqh.crawler.common.Constants.SIZE;
import static com.hxqh.crawler.common.Constants.TOTAL_PAGES;

/**
 * @author Ocean Lin
 * Created by Ocean lin on 2017/7/9.
 */
@Component
public class IqiyiTimer {
    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerSoapURLRepository soapURLRepository;

    /**
     * 爬取爱奇艺电影数据
     */
    @Scheduled(cron = "0 30 22 * * ?")
    public void iqiyiFilm() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK3)) {


                // 1. 从数据库获取待爬取链接
                List<CrawlerURL> crawlerURLS = crawlerURLRepository.findFilm();

                List<CrawlerURL> urlList = crawlerURLS.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                        -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

                Integer partitionNUm = urlList.size() / Constants.IQIYI_THREAD_NUM + 1;
                List<List<CrawlerURL>> lists = ListUtils.partition(urlList, partitionNUm);
                ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_THREAD_NUM,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

                for (List<CrawlerURL> l : lists) {
                    service.execute(new PersistFilm(l, crawlerProblemRepository, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

//                // 2. 上传至HDFS
//                try {
//                    HdfsUtils.persistToHDFS("-iqiyi", Constants.FILE_LOC);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 爬取爱奇艺电视剧数据
     */
    @Scheduled(cron = "0 50 0 * * ?")
    public void iqiyiSoap() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK3)) {
                List<CrawlerSoapURL> soapURLList = soapURLRepository.findAll();

                List<CrawlerSoapURL> urlList = soapURLList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                        -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

                Integer partitionNUm = urlList.size() / Constants.IQIYI_THREAD_NUM + 1;
                List<List<CrawlerSoapURL>> lists = ListUtils.partition(urlList, partitionNUm);

                ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_THREAD_NUM,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

                for (List<CrawlerSoapURL> l : lists) {
                    service.execute(new PersistFilm(l, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }
//                // 2. 上传至HDFS
//                try {
//                    HdfsUtils.persistToHDFS("-soap-iqiyi", Constants.FILE_LOC_SOAP);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 爬取爱奇艺综艺节目数据
     * <p>
     * 改为每天17:30执行
     */
    @Scheduled(cron = "0 30 17 * * ?")
    public void iqiyiVariety() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
                // 需要增加分页 vid
                Sort sort = new Sort(Sort.Direction.DESC, "vid");

                Pageable pageable = new PageRequest(PAGE, SIZE, sort);
                Page<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll(pageable);
                Integer totalPages = varietyURLList.getTotalPages();

                // todo 2w 网络资源不足
                if (totalPages > TOTAL_PAGES) {
                    totalPages = TOTAL_PAGES;
                }

                for (int i = 0; i < totalPages; i++) {
                    pageable = new PageRequest(i, SIZE, sort);
                    Page<CrawlerVarietyURL> batch = crawlerVarietyURLRepository.findAll(pageable);
                    List<CrawlerVarietyURL> content = batch.getContent();

                    List<CrawlerVarietyURL> urlList = content.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                            -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

                    Integer partitionNUm = urlList.size() / Constants.IQIYI_VARIETY_THREAD_NUM + 1;
                    List<List<CrawlerVarietyURL>> lists = ListUtils.partition(urlList, partitionNUm);

                    ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_VARIETY_THREAD_NUM,
                            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

                    for (List<CrawlerVarietyURL> l : lists) {
                        service.execute(new PersistFilm(l, crawlerVarietyURLRepository, systemService));
                    }
                    service.shutdown();
                    while (!service.isTerminated()) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
