package com.hxqh.crawler.controller;

import com.google.gson.JsonObject;
import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import com.hxqh.crawler.util.HostUtils;
import com.hxqh.crawler.util.ReadUrlUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * spark2 执行
 * <p>
 * Created by Ocean lin on 2018/2/7.
 *
 * @author Ocean Lin
 */
@Component
public class MaoYanTimer {

    //每一个小时执行一次
    @Scheduled(cron = "0 0 * * * ?")
    public void jdData() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
                while (true) {
                    //  获取当前时间
                    String dateString = DateUtils.getTodayDate();
                    try {
                        String url = "https://box.maoyan.com/promovie/api/box/second.json";
                        JsonObject xpath = ReadUrlUtils.getXpath(url);
                        FileUtils.writeStrToFile(xpath.toString(),
                                Constants.MAOYAN_PATH + Constants.FILE_SPLIT + dateString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}