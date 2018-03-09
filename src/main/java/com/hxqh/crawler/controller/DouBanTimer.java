package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.HostUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */

@Component
public class DouBanTimer {

    @Scheduled(cron = "0 0 1 * * ?")
    public void iqiyiUrlList() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK1)) {


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
