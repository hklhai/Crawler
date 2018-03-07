package com.hxqh.crawler.test;

import com.hxqh.crawler.repository.BaiduInfoRepository;
import com.hxqh.crawler.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ocean lin on 2018/1/17.
 */
//@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
public class GetURLIqiyiTest {

    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private BaiduInfoRepository baiduInfoRepository;

//    @Test
//    public void deleteIqiyiFilm() {
//        crawlerService.deleteIqiyiFilm();
//    }
//
//    @Test
//    public void delTencentFilm() {
//        crawlerService.delTencentFilm();
//    }

//    @Test
//    public void baiduInfoFindAll() {
//        List<BaiduInfo> all = baiduInfoRepository.findAll();
//        System.out.println(all.size());
//    }



}
