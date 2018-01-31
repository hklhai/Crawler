package com.hxqh.crawler.controller;

import com.hxqh.crawler.repository.CrawlerBookURLRepository;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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


        return "crawler/notice";
    }


    /**
     * http://127.0.0.1:8090/jd/test
     *
     * @return
     */
    @RequestMapping("/test")
    public String test() {

        String str = "https://v.qq.com/x/cover/dhdm0lalbqrgtru.html";

        String html = null;
        try {
            html = CrawlerUtils.fetchHTMLContent(str, 6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(html);

        Elements elementsByClass = doc.getElementsByClass("video_title _video_title");

        String title = elementsByClass.text();
        System.out.println(title);


        return "crawler/notice";
    }

}
