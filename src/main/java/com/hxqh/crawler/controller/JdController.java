package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistJdBook;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/1/29.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/jd")
public class JdController {

    private final static Integer JD_PAGE_NUM = 6;

    @Autowired
    private CrawlerBookURLRepository crawlerBookURLRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    /**
     * http://127.0.0.1:8090/jd/bookUrl
     *
     * @return
     */
    @RequestMapping("/bookUrl")
    public String bookUrl() {


        return "crawler/notice";
    }

    /**
     * http://127.0.0.1:8090/jd/jdBookData
     *
     * @return
     */
    @RequestMapping("/jdBookData")
    public String jdBookData() {
        // 1. 从数据库获取待爬取链接
        List<CrawlerBookURL> crawlerBookURLList = crawlerBookURLRepository.findAll();
        List<List<CrawlerBookURL>> lists = ListUtils.partition(crawlerBookURLList, Constants.JD_PARTITION_NUM);

        ExecutorService service = Executors.newFixedThreadPool(Constants.JD_THREAD_NUM);
        for (List<CrawlerBookURL> list : lists) {
            service.execute(new PersistJdBook(list, crawlerProblemRepository, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }
/**
 *         ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
 .setNameFormat("demo-pool-%d").build();
 //Common Thread Pool
 ExecutorService pool = new ThreadPoolExecutor(5, 200,
 0L, TimeUnit.MILLISECONDS,
 new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
 for (List<CrawlerBookURL> list : lists) {
 pool.execute(new PersistJdBook(list, crawlerProblemRepository, systemService));
 }
 pool.shutdown();//gracefully shutdown
 */

        // 2. 上传至HDFS
        try {
            HdfsUtils.persistToHDFS("-jd", Constants.BOOK_JD_FILE_LOC);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "crawler/notice";
    }


}
