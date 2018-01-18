package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.PubMap;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Ocean lin on 2017/7/3.
 */
@RepositoryDefinition(domainClass = PubMap.class, idClass = Integer.class)
public interface PubMapRepository {
    List<PubMap> findByPubnameStartingWithAndPubtype(String pubName, String pubType);


    List<PubMap> findByPubnameEndingWithAndPubidLessThan(String pubName, Integer id);

    List<PubMap> findByPubnameInOrPubidIn(List<String> names, List<Integer> ids);


    @Query("select o from PubMap o where o.pubid=(select max(p.pubid) from PubMap p )")
    PubMap getMaxPubMap();

    @Query("select o from PubMap o where o.pubname=?1 and o.pubid=?2")
    List<PubMap> queryParam(String pubName, Integer id);


    @Query("select o from PubMap o where o.pubname=:pubname and o.pubid=:pubid")
    List<PubMap> queryParamter(@Param("pubname") String pubname, @Param("pubid") Integer pubid);

    @Query("select o from PubMap o where o.pubname like %?1%")
    List<PubMap> queryLike(String pubname);

    @Query("select o from PubMap o where o.pubname like %:pubname%")
    List<PubMap> queryLikeParam(@Param("pubname") String pubname);

    @Query(nativeQuery=true,value = "select count(1) from pub_map ")
    long getCount();


}
