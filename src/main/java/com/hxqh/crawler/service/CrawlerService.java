package com.hxqh.crawler.service;

import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.BaiduInfo;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;

import java.util.List;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Ocean lin
 */

public interface CrawlerService {

    void deleteIqiyiFilm();

    void delTencentFilm();

    void deleteIqiyiSoap();

    void save(BaiduInfo baiduInfo);

    void persistVarietyUrlList(List<CrawlerVarietyURL> varietyURLList);

    void persistEachVarietyUrlList(List<CrawlerVariety> list);


    void persistFilmUrl(Map<String, URLInfo> hrefMap);

    void saveSoap(List<CrawlerSoapURL> soapURLList);
}
