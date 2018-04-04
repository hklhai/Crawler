package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.util.HostUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */

@Component
public class DouBanTimer {

    @Scheduled(cron = "0 0 12 * * ?")
    public void douBan() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
