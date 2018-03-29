package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ocean lin on 2018/1/18.
 */
@Repository
public interface CrawlerURLRepository extends JpaRepository<CrawlerURL, String> {

    /**
     * 删除爱奇艺电影数据
     */
    @Query("select o from CrawlerURL o where o.category ='film' and o.platform ='iqiyi' ")
    List<CrawlerURL> findFilm();

    @Query("select o from CrawlerURL o where o.sorted = 'hot' and o.platform = 'iqiyi' ")
    List<CrawlerURL> findHotFilm();

    @Query("select o from CrawlerURL o where o.category='film' and o.platform='tencent'")
    List<CrawlerURL> findTencentFilm();

    @Modifying
    @Query("delete from CrawlerURL o where o.category ='film' and o.platform ='tencent'")
    void delTencentFilm();

    @Modifying
    @Query("delete from CrawlerURL o where o.category ='film' and o.platform ='iqiyi' ")
    void deleteIqiyiFilm();

    /**
     * 删除爱奇艺电视剧数据
     */
    @Modifying
    @Query("delete from CrawlerURL o where o.category ='soap' and o.platform ='iqiyi' ")
    void deleteIqiyiSoap();

}
