package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistDouBan;
import com.hxqh.crawler.model.CrawlerDoubanScore;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */

@Controller
@RequestMapping("/douban")
public class DouBanController {


    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;


    /**
     * 根据影视数据爬取豆瓣评分、评论量持久化至MYSQL
     */
    @RequestMapping("/filmDouBan")
    public String filmDouBan() {


        return "crawler/notice";
    }


}
