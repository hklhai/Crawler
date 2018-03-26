package com.hxqh.crawler.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.RealTimeMovie;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import com.hxqh.crawler.util.NumUtils;
import com.hxqh.crawler.util.ReadUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ocean lin on 2018/2/7.
 *
 * @author Lin
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class MaoYanCrawlerTest {


    @Autowired
    private SystemService systemService;

    //    @Test
    public void url() {
        while (true) {
            //  获取当前时间
            String dateString = DateUtils.getTodayDate();

            try {
                Date d = new Date();
                SimpleDateFormat myFmt = new SimpleDateFormat("mmss");
                String mmss = myFmt.format(d);
                if ("0000".equals(mmss)) {
                    String url = "https://box.maoyan.com/promovie/api/box/second.json";
                    JsonObject xpath = ReadUrlUtils.getXpath(url);
                    FileUtils.writeStrToFile(xpath.toString(),
                            Constants.MAOYAN_PATH + Constants.FILE_SPLIT + dateString);

                    System.out.println(xpath);
                    System.out.println();
                    //Thread.sleep(Constants.ONE_HOUR);  // 每3s版本
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    //    @Test
    public void parse() {

    }


}