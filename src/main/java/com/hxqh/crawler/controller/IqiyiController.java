package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.HdfsUtils;
import org.apache.commons.collections4.ListUtils;
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
 * @author Ocean Lin
 *         <p>
 *         Created by Ocean lin on 2017/7/1.
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
        List<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll();

        List<CrawlerVarietyURL> urlList = varietyURLList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

        Integer partitionNUm = urlList.size() / Constants.IQIYI_VARIETY_THREAD_NUM + 1;
        List<List<CrawlerVarietyURL>> lists = ListUtils.partition(urlList, partitionNUm);

        ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_VARIETY_THREAD_NUM);

        for (List<CrawlerVarietyURL> l : lists) {
            service.execute(new PersistFilm(l, crawlerVarietyURLRepository, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }

        // 2. 上传至HDFS
        try {
            HdfsUtils.persistToHDFS("-variety-iqiyi", Constants.FILE_LOC_VARIETY);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "crawler/notice";
    }

    @RequestMapping("/test")
    public String test() throws Exception {


        return "crawler/notice";
    }


}


