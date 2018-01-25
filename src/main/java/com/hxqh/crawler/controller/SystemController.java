package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.User;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.DateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Ocean lin on 2017/7/1.
 */
@Controller
@RequestMapping("/system")
public class SystemController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    /**
     * 页面跳转接口
     * http://127.0.0.1:8090/system/user
     *
     * @return
     */
    @RequestMapping("/user")
    public String user() {
        return "user/index";
    }

    /**
     * 数据接口
     * http://127.0.0.1:8090/system/userData?name=xdm
     *
     * @param name 用户名
     * @return
     */
    @RequestMapping("userData")
    @ResponseBody
    public User userData(@RequestParam(value = "name") String name) {
        return systemService.findUserById(name);
    }


}


