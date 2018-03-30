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
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0 35 13 * * ?")
    public void iqiyiFilm() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK3)) {

                // 1. 从数据库获取待爬取链接
                List<CrawlerURL> crawlerURLS = crawlerURLRepository.findFilm();
                Integer partitionNUm = crawlerURLS.size() / Constants.IQIYI_THREAD_NUM + 1;
                List<List<CrawlerURL>> lists = ListUtils.partition(crawlerURLS, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

                for (List<CrawlerURL> l : lists) {
                    service.execute(new PersistFilm(l, crawlerProblemRepository, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-iqiyi", Constants.FILE_LOC);
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


    /**
     * 爬取爱奇艺电视剧数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void iqiyiSoap() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
                List<CrawlerSoapURL> soapURLList = soapURLRepository.findAll();

                Integer partitionNUm = soapURLList.size() / Constants.IQIYI_THREAD_NUM + 1;
                List<List<CrawlerSoapURL>> lists = ListUtils.partition(soapURLList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

                for (List<CrawlerSoapURL> l : lists) {
                    service.execute(new PersistFilm(l, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-soap-iqiyi", Constants.FILE_LOC_SOAP);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 爬取爱奇艺综艺节目数据
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void iqiyiVariety() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK1)) {
                List<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll();

                Integer partitionNUm = varietyURLList.size() / Constants.IQIYI_VARIETY_THREAD_NUM + 1;
                List<List<CrawlerVarietyURL>> lists = ListUtils.partition(varietyURLList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_VARIETY_THREAD_NUM);

                for (List<CrawlerVarietyURL> l : lists) {
                    service.execute(new PersistFilm(l, crawlerVarietyURLRepository, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-variety-iqiyi", Constants.FILE_LOC_VARIETY);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
