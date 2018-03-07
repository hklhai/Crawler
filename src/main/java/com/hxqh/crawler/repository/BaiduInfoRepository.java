package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.BaiduInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/3/6.
 *
 * @author Ocean lin
 */
@Repository
public interface BaiduInfoRepository extends JpaRepository<BaiduInfo, Integer> {

}
