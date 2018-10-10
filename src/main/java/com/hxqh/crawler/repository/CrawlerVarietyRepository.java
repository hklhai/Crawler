package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerVariety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/3/16.
 *
 * @author Ocean lin
 */
@Repository
public interface CrawlerVarietyRepository extends JpaRepository<CrawlerVariety, Integer> {
    @Modifying
    @Query("delete from CrawlerVariety o ")
    void deleteIqiyiVariety();
}
