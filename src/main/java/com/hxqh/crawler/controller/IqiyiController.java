package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
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
 * @author Ocean Lin
 *
 * Created by Ocean lin on 2017/7/1.
 */
@Controller
@RequestMapping("/iqiyi")
public class IqiyiController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private CrawlerSoapURLRepository crawlerSoapURLRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerVarietyRepository crawlerVarietyRepository;
    @Autowired
    private CrawlerSoapURLRepository soapURLRepository;


    @RequestMapping("/filmUrl")
    public String filmUrl() throws Exception {



        return "crawler/notice";
    }


    /**
     * 爬取电影、影视、综艺数据并上传至HDFS
     * http://127.0.0.1:8666/iqiyi/filemData
     *
     * @return
     */
    @RequestMapping("/filmData")
    public String filmData() throws Exception {



        return "crawler/notice";
    }


    /**
     * 持久化每一集电视剧
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/soapUrl")
    public String soapUrl() throws Exception {


        return "crawler/notice";
    }





    /**
     * 每部综艺节目链接爬取
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/eachVarietyUrl")
    public String eachVarietyUrl() throws Exception {



        return "crawler/notice";
    }





    /**
     * 每部综艺节目每集爬取
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/varietyUrl")
    public String varietyUrl() throws Exception {



        return "crawler/notice";
    }




    /**
     * 持久化综艺内容
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/varietyDataUrl")
    public String varietyDataUrl() throws Exception {


        return "crawler/notice";
    }

}


