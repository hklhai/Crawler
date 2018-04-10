package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.VBaiduCrawler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/4/10.
 *
 * @author Ocean lin
 */
@Repository
public interface VBaiduCrawlerRepository extends JpaRepository<VBaiduCrawler, Integer> {
}
