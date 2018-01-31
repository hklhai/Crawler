package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerBookURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ocean lin on 2018/1/29.
 *
 * @author Ocean lin
 */
@Repository
public interface CrawlerBookURLRepository extends JpaRepository<CrawlerBookURL, String> {

    @Query("select o from CrawlerBookURL o where o.platform = 'jd' ")
    List<CrawlerBookURL> findBookUrl();
}
