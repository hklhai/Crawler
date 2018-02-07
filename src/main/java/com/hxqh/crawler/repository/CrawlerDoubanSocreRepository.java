package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerDoubanScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlerDoubanSocreRepository extends JpaRepository<CrawlerDoubanScore,String> {
    @Query("select o from CrawlerDoubanScore o where o.title = :film ")
    List<CrawlerDoubanScore> findScore(@Param("film") String film);

}
