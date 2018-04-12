package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.VDouBanCrawlerBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/4/12.
 *
 * @author Ocean lin
 */
@Repository
public interface VDouBanCrawlerBookRepository extends JpaRepository<VDouBanCrawlerBook, Long> {
}
