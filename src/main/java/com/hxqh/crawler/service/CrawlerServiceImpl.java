package com.hxqh.crawler.service;

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
}
