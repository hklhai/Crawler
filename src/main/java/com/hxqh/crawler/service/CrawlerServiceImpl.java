package com.hxqh.crawler.service;

import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.*;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.util.CrawlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Ocean lin
 */
@Service("crawlerService")
public class CrawlerServiceImpl implements CrawlerService {


    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private BaiduInfoRepository baiduInfoRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerVarietyRepository crawlerVarietyRepository;
    @Autowired
    private CrawlerSoapURLRepository crawlerSoapURLRepository;
    @Autowired
    private CrawlerBookURLRepository crawlerBookURLRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteIqiyiFilm() {
        crawlerURLRepository.deleteIqiyiFilm();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteIqiyiSoap() {
        crawlerURLRepository.deleteIqiyiSoap();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delTencentFilm() {
        crawlerURLRepository.delTencentFilm();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(BaiduInfo baiduInfo) {
        baiduInfoRepository.save(baiduInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteIqiyiVariety() {
        crawlerVarietyURLRepository.deleteIqiyiVariety();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void persistVarietyUrlList(List<CrawlerVarietyURL> varietyURLList) {
        crawlerVarietyURLRepository.save(varietyURLList);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void persistEachVarietyUrlList(List<CrawlerVariety> urlList) {
        crawlerVarietyRepository.save(urlList);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void persistFilmUrl(Map<String, URLInfo> hrefMap) {
        // 清除所有mysql数据
        deleteIqiyiFilm();
        CrawlerUtils.persistCrawlerURL(hrefMap, crawlerURLRepository);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSoap(List<CrawlerSoapURL> soapURLList) {
        // 清除所有mysql数据
        deleteIqiyiSoap();
        crawlerSoapURLRepository.save(soapURLList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void persistBookUrl(List<CrawlerBookURL> crawlerURLList) {

        /**
         * 清除所有mysql数据
         */
        crawlerBookURLRepository.deleteJdBooks();
        crawlerBookURLRepository.save(crawlerURLList);
    }


}
