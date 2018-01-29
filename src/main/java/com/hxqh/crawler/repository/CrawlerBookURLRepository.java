package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerBookURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/1/29.
 *
 * @author Ocean lin
 */
@Repository
public interface CrawlerBookURLRepository extends JpaRepository<CrawlerBookURL, String> {
}
