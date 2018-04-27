package com.hxqh.crawler.test;

import com.hxqh.crawler.model.PubMap;
import com.hxqh.crawler.repository.PubMapRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ocean lin on 2017/7/3.
 *
 * @author Lin
 */

//@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
public class PubMapRepositoryTest {
    @Resource
    private PubMapRepository pubMapRepository;

//    @Test
//    public void testFindByPubnameStartingWithAndPubtype() {
//        List<PubMap> pubMapList = pubMapRepository.findByPubnameStartingWithAndPubtype("中国", "出版社-示范");
//        Assert.assertTrue(pubMapList.size() > 0);
//    }
//
//    @Test
//    public void testFindByPubnameEndingWithAndPubidLessThan() {
//        List<PubMap> pubMapList = pubMapRepository.findByPubnameEndingWithAndPubidLessThan("出版社", 20);
//        Assert.assertTrue(pubMapList.size() > 0);
//    }
//
//    @Test
//    public void testFindByPubnameInOrPubidIn() {
//
//        List<String> list = new ArrayList<>();
//        list.add("九州出版社");
//        list.add("中国科技出版传媒股份有限公司");
//
//        List<Integer> ids = new ArrayList<>();
//        ids.add(1);
//        ids.add(10);
//        ids.add(23);
//
//        List<PubMap> pubMapList = pubMapRepository.findByPubnameInOrPubidIn(list, ids);
//        Assert.assertTrue(pubMapList.size() > 0);
//    }
//
//    @Test
//    public void testGetMaxPubMap() {
//        PubMap maxPubMap = pubMapRepository.getMaxPubMap();
//        Assert.assertTrue(maxPubMap.getPubid().equals(20));
//    }
//
//    @Test
//    public void testQueryParam() {
//        List<PubMap> pubMapList = pubMapRepository.queryParam("九州出版社", 5);
//        Assert.assertTrue(pubMapList.size() > 0);
//        List<PubMap> pubMaps = pubMapRepository.queryParamter("九州出版社", 5);
//        Assert.assertTrue(pubMaps.size() > 0);
//    }
//
//
//    @Test
//    public void testQueryLike() {
//        List<PubMap> pubMapList = pubMapRepository.queryLike("出版社");
//        Assert.assertTrue(pubMapList.size() > 0);
//        List<PubMap> pubMaps = pubMapRepository.queryLikeParam("出版社");
//        Assert.assertTrue(pubMaps.size() > 0);
//    }
//
//
//    @Test
//    public void testGetCount() {
//        long count = pubMapRepository.getCount();
//        Assert.assertTrue(count > 0);
//    }


}
