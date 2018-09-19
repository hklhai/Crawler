package com.hxqh.crawler.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.RealTimeMovie;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * spark2 执行猫眼每小时爬取数据
 * spark4 执行猫眼每3秒爬取数据
 * <p>
 * Created by Ocean lin on 2018/2/7.
 *
 * @author Ocean Lin
 */
@Component
public class MaoYanTimer {

    private static final String url = "https://box.maoyan.com/promovie/api/box/second.json";

    @Autowired
    private SystemService systemService;

    //每一个小时执行一次
    @Scheduled(cron = "0 */60 * * * * ")
    public void maoYanOnehour() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
                //  获取当前时间
                String dateString = DateUtils.getTodayDate();
                try {

                    JsonObject xpath = ReadUrlUtils.getXpath(url);
                    JSONObject json = JSONObject.parseObject(xpath.toString());
                    JSONArray array = json.getJSONObject("data").getJSONArray("list");

                    List<RealTimeMovie> movieArrayList = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject jo = array.getJSONObject(i);

                        // 来源、电影名称、实时综合票房（万）、累计票房、实时分账票房、累计分账票房、排片场次、新增时间
                        String boxInfo = jo.getString("boxInfo");
                        String sumBoxInfo = jo.getString("sumBoxInfo");
                        String splitBoxInfo = jo.getString("splitBoxInfo");
                        String splitSumBoxInfo = jo.getString("splitSumBoxInfo");
                        Integer showInfo = Integer.valueOf(jo.getString("showInfo"));
                        String releaseInfo = jo.getString("releaseInfo");

                        RealTimeMovie realTimeMovie = new RealTimeMovie(Constants.MAO_YAN,
                                jo.getString("movieName"),
                                NumUtils.getNumber(boxInfo),
                                NumUtils.getNumber(sumBoxInfo),
                                NumUtils.getNumber(splitBoxInfo),
                                NumUtils.getNumber(splitSumBoxInfo),
                                releaseInfo,
                                new Date(),
                                showInfo
                        );
                        movieArrayList.add(realTimeMovie);
                    }
                    systemService.addMaoYanList(movieArrayList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    //3s执行一次
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void maoYanPerThreeSecond() {
//        try {
//            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
//                //  获取当前时间
//                String dateString = DateUtils.getTodayDate();
//                try {
//                    JsonObject xpath = ReadUrlUtils.getXpath(url);
//                    FileUtils.writeStrToFile(xpath.toString() + "\n",
//                            Constants.MAOYAN_THREE_SECOND_PATH + Constants.FILE_SPLIT + dateString);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
