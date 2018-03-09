package com.hxqh.crawler.test;


import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.collections4.ListUtils;

/**
 * Created by Ocean lin on 2018/1/18.
 *
 * @author Lin
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class IqiyiCrawlerTest {

    @Resource
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private SystemService systemService;

    @Test
    public void persist() {


    }


}

