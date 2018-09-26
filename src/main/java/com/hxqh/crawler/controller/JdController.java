package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistJdBook;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

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
    @Autowired
    private CrawlerService crawlerService;

    /**
     * http://127.0.0.1:8666/jd/bookUrl
     *
     * @return
     */
    @RequestMapping("/bookUrl")
    public String bookUrl() {


        return "crawler/notice";
    }

    /**
     * http://127.0.0.1:8666/jd/jdBookData
     *
     * @return
     */
    @RequestMapping("/jdBookData")
    public String jdBookData() {
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


        return "crawler/notice";
    }


    /**
     * http://127.0.0.1:8666/jd/test
     *
     * @return
     */
    @RequestMapping("/test")
    public String test() {

        String str = "https://item.jd.com/11672203.html";

        String html = null;
        try {
            html = CrawlerUtils.fetchHTMLContentByPhantomJs(str, 6);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(html);

        Elements authorElement = doc.getElementById("p-author").select("a");
        String author = authorElement.text();
        System.out.println(author);


        return "crawler/notice";
    }

}
