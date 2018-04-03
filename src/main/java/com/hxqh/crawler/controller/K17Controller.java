package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistLiterature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/17k")
public class K17Controller {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;


    /**
     * 爬取17K网络文学URL
     *
     * @return
     */
    @RequestMapping("/literatureUrl")
    public String literatureUrl() {


        return "crawler/notice";
    }


    /**
     * 爬取17K网络文学数据
     *
     * @return
     */
    @RequestMapping("/literatureDataUrl")
    public String literatureDataUrl() {


        return "crawler/notice";
    }

}
