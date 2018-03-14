package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerLiteratureURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */

@Repository
public interface CrawlerLiteratureURLRepository extends JpaRepository<CrawlerLiteratureURL, Integer> {
}
