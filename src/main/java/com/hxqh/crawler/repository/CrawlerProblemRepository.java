package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/1/19.
 */
@Repository
public interface CrawlerProblemRepository extends JpaRepository<CrawlerProblem, String> {
    CrawlerProblem findByUrl(String url);
}
