package com.hxqh.crawler.service;

import com.hxqh.crawler.model.BaiduInfo;
import com.hxqh.crawler.repository.BaiduInfoRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    @Override
    public void deleteIqiyiFilm() {
        crawlerURLRepository.deleteIqiyiFilm();
    }

    @Transactional
    @Override
    public void delTencentFilm() {
        crawlerURLRepository.delTencentFilm();
    }

    @Transactional
    @Override
    public void save(BaiduInfo baiduInfo) {
        baiduInfoRepository.save(baiduInfo);
    }


}
