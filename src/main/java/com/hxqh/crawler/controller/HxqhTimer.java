package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.DateUtils;
//import org.apache.commons.collections4.ListUtils;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2017/7/9.
 */
@Component
public class HxqhTimer {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

//    //每天0点10分触发
//    @Scheduled(cron = "0 10 0 * * ?")
//    public void iqiyi() {
//        // 1. 从数据库获取待爬取链接
//        List<String> hrefList = new ArrayList<>();
//
//        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findAll();
//        for (CrawlerURL crawlerURL : crawlerURLS) {
//            hrefList.add(crawlerURL.getUrl());
//        }
//
//        List<List<String>> lists = ListUtils.partition(hrefList, Constants.PARTITION_NUM);
//
//        ExecutorService service = Executors.newFixedThreadPool(Constants.THREAD_NUM);
//
//        for (List<String> l : lists) {
//            service.execute(new PersistFilm(l, crawlerProblemRepository));
//        }
//        service.shutdown();
//
//        while (!service.isTerminated()) {
//        }
//
//
//        // 2.上传至HSDF
//        try {
//            persistToHDFS("-iqiyi", Constants.FILE_LOC);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void persistToHDFS(String paltform, String loc) throws URISyntaxException, IOException {
//        Configuration conf = new Configuration();
//        URI uri = new URI(Constants.HDFS_URL);
//        FileSystem fs = FileSystem.get(uri, conf);
//        String path = Constants.SAVE_PATH + DateUtils.getTodayDate() + paltform;
//        Path resP = new Path(path);
//        String location = loc + Constants.FILE_SPLIT +
//                DateUtils.getTodayYear() + Constants.FILE_SPLIT + DateUtils.getTodayMonth();
//        Path destP = new Path(location);
//        if (!fs.exists(destP)) {
//            fs.mkdirs(destP);
//        }
//        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
//        fs.copyFromLocalFile(resP, destP);
//        System.out.println("upload file " + name + " to HDFS");
//        fs.close();
//    }


}
