package com.hxqh.crawler.test;

import com.hxqh.crawler.repository.CrawlerURLRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Ocean lin on 2018/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GetURLIqiyiTest {


    @Autowired
    private CrawlerURLRepository crawlerURLRepository;


    @Test
    public void testAppAndPersist() {

    }

}
