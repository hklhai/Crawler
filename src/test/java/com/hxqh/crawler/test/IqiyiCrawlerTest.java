package com.hxqh.crawler.test;


import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by Ocean lin on 2018/1/18.
 *
 * @author Lin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IqiyiCrawlerTest {

    @Resource
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerVarietyRepository crawlerVarietyRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerSoapURLRepository soapURLRepository;


    @Test
    public void persist() {
//        List<CrawlerSoapURL> soapURLList = soapURLRepository.findAll();

//        List<CrawlerURL> hrefList = crawlerURLRepository.findFilm();
        List<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll();

        StringBuilder stringBuilder = new StringBuilder(300);


    }


}

