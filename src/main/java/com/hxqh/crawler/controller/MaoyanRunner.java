package com.hxqh.crawler.controller;

import com.google.gson.JsonObject;
import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import com.hxqh.crawler.util.HostUtils;
import com.hxqh.crawler.util.ReadUrlUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ocean lin on 2018/2/7.
 *
 * @author Ocean lin
 */
@Component
public class MaoyanRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments var1) throws Exception {
        if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
            while (true) {
                //  获取当前时间
                String dateString = DateUtils.getTodayDate();

                try {
                    Date d = new Date();
                    SimpleDateFormat myFmt = new SimpleDateFormat("mmss");
                    String mmss = myFmt.format(d);
                    // 整点下载一次
                    if ("0000".equals(mmss)) {
                        String url = "https://box.maoyan.com/promovie/api/box/second.json";
                        JsonObject xpath = ReadUrlUtils.getXpath(url);
                        FileUtils.writeStrToFile(xpath.toString(),
                                Constants.MAOYAN_PATH + Constants.FILE_SPLIT + dateString);

                        //System.out.println(xpath);
                        //System.out.println();
                        //Thread.sleep(Constants.THREE_SECOND);  // 每3s版本

                        Thread.sleep(Constants.ONE_HOUR);  // 每1h版本
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
