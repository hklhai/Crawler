package com.hxqh.crawler.test;


import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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

//    @Test
    public void persist() {

        List<String> list = Arrays.asList("http://www.iqiyi.com/a_19rrgubthd.html");

        for (int i = 0; i < list.size(); i++) {
            String url = list.get(i);
            persistVarietyUrlList(url, "hot");
        }
    }


    private void persistVarietyUrlList(String url, String type) {
        List<CrawlerVarietyURL> soapURLList = new ArrayList<>();
        try {
            List<CrawlerVarietyURL> varietyURLList = CrawlerUtils.fetchHTMLContent(url, 3, "");
            for (CrawlerVarietyURL varietyURL : varietyURLList) {
                System.out.println(varietyURL);
            }
            System.out.println(varietyURLList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

