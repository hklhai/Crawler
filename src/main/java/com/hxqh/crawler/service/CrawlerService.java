package com.hxqh.crawler.service;

import com.hxqh.crawler.model.BaiduInfo;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Ocean lin
 */

public interface CrawlerService {

    void deleteIqiyiFilm();

    void delTencentFilm();

    void save(BaiduInfo baiduInfo);
}
