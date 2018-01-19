package com.hxqh.crawler.controller;

import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.User;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2017/7/1.
 */
@Controller
@RequestMapping("/system")
public class SystemController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    /**
     * 页面跳转接口
     *
     * @return
     */
    @RequestMapping("/user")
    public String user() {
        return "user/index";
    }

    /**
     * 数据接口
     * http://127.0.0.1:8080/system/userData?name=xdm
     *
     * @param name 用户名
     * @return
     */
    @RequestMapping("userData")
    @ResponseBody
    public User userData(@RequestParam(value = "name") String name) {
        return systemService.findUserById(name);
    }

    @RequestMapping("/iqiyi")
    public String iqiyiFilm() {
        // 1. 从数据库获取待爬取链接
        List<String> hrefList = new ArrayList<>();

        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findAll();
        for (CrawlerURL crawlerURL : crawlerURLS) {
            hrefList.add(crawlerURL.getUrl());
        }

        List<List<String>> lists = ListUtils.partition(hrefList, 222);

//        List<String> subList = hrefList.subList(0, 10);
//        List<List<String>> lists = ListUtils.partition(subList, 3);
        ExecutorService service = Executors.newFixedThreadPool(4);

        for (List<String> l : lists) {
            service.execute(new PersistFilm(l));
        }
        service.shutdown();

        while (!service.isTerminated()) {
        }

        return "crawler/notice";
    }


}


