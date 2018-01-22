package com.hxqh.crawler.controller;

import com.hxqh.crawler.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Ocean lin on 2017/7/9.
 */
@Component
public class HxqhTimer {

    @Autowired
    private SystemService systemService;


    //每天早八点到晚八点，间隔5分钟执行任务
    @Scheduled(cron = "0 */5 * * * * ")
    public void iqiyi() {
        try {

        } catch (Exception e) {
            //TODO 日志功能
            e.printStackTrace();
        }
    }


}
