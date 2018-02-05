package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HostUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            html = CrawlerUtils.fetchHTMLContent(str, 6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(html);

        Elements authorElement = doc.getElementById("p-author").select("a");
        String author = authorElement.text();
        System.out.println(author);


        return "crawler/notice";
    }

}