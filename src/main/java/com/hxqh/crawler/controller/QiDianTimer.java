package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistLiterature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
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
 * Created by Ocean lin on 2018/3/22.
 *
 * @author Ocean lin
 */
@Component
public class QiDianTimer {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;


//    //每天1点0分触发
//    @Scheduled(cron = "0 0 1 * * ?")
//    public void qiDianUrl() {
//        try {
//            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
//
//            }
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //每天4点0分触发
    @Scheduled(cron = "0 0 4 * * ?")
    public void qiDianData() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {


                List<CrawlerLiteratureURL> varietyURLList = crawlerLiteratureURLRepository.findAll();
                Integer partitionNUm = varietyURLList.size() / Constants.QIDIAN_THREAD_NUM + 1;
                List<List<CrawlerLiteratureURL>> lists = ListUtils.partition(varietyURLList, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

                for (List<CrawlerLiteratureURL> list : lists) {
                    service.execute(new PersistLiterature(systemService, list));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-literature-qidian", Constants.FILE_LOC);
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