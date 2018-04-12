package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.VDouBanCrawlerFilm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/4/12.
 *
 * @author Ocean lin
 */
@Repository
public interface VDouBanCrawlerFilmRepository extends JpaRepository<VDouBanCrawlerFilm, Long> {
}
