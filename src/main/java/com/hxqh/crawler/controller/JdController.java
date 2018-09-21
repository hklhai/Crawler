package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistJdBook;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.HdfsUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        ExecutorService service = Executors.newFixedThreadPool(Constants.JD_THREAD_NUM);
        for (List<CrawlerBookURL> list : lists) {
            service.execute(new PersistJdBook(list, crawlerProblemRepository, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
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
