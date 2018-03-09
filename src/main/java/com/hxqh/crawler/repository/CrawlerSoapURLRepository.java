package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/1/18.
 */
@Repository
public interface CrawlerSoapURLRepository extends JpaRepository<CrawlerSoapURL, Integer> {

}
