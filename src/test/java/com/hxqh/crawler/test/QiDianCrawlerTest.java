package com.hxqh.crawler.test;

import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */

//@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
public class QiDianCrawlerTest {

    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;

    @Autowired
    private Environment env;

    /**
     * 20 * 20
     */
//    @Test
//    public void test() {
//        String property = env.getProperty("spring.mvc.static-path-pattern");
//        System.out.println(property);
//    }


}
