package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistTencentFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.CrawlerService;
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
 * spark4 执行
 */
@Component
public class TencentTimer {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private CrawlerService crawlerService;


    /**
     * 1. 获取爬取列表前先将数据写入ES
     * 2. 清除所有mysql数据
     * 3. 进行爬取
     */
    // 每个星期日16点00分
    @Scheduled(cron = "0 0 16 ? * SUN")
    public void tencentUrlList() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK1)) {


            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //每天2点0分触发
    @Scheduled(cron = "0 0 2 * * ?")
    public void tencent() {

        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK1)) {

                // 1. 从数据库获取待爬取链接
                List<CrawlerURL> crawlerURLS = crawlerURLRepository.findTencentFilm();


                List<List<CrawlerURL>> lists = ListUtils.partition(crawlerURLS, Constants.TENCENT_PARTITION_NUM);

                ExecutorService service = Executors.newFixedThreadPool(Constants.TENCENT_THREAD_NUM);

                for (List<CrawlerURL> l : lists) {
                    service.execute(new PersistTencentFilm(l, crawlerProblemRepository, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-tencent", Constants.FILE_LOC);
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
