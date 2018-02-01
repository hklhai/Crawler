package com.hxqh.crawler.controller;

import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "crawler/notice";
    }
}
