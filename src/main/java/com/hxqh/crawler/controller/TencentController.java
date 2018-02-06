package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistTencentFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/tencent")
public class TencentController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;


    @RequestMapping("/filmeUrl")
    public String filmeUrl() {
        return "crawler/notice";
    }



    @RequestMapping("/filemData")
    public String filemData() {

        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {

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








        return "crawler/notice";
    }
}
