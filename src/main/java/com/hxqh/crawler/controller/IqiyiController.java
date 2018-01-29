package com.hxqh.crawler.controller;

import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author Ocean Lin
 * Created by Ocean lin on 2017/7/1.
 */
@Controller
@RequestMapping("/iqiyi")
public class IqiyiController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;


    /**
     * 生成待爬取页面集合 电影
     * <p>
     * http://list.iqiyi.com/www/1/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/1/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/1/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/1/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * http://list.iqiyi.com/www/1/-------------8-1-1-iqiyi--.html  评分
     * http://list.iqiyi.com/www/1/-------------8-2-1-iqiyi--.html  评分
     * <p>
     * <p>
     * <p>
     * 生成待爬取页面集合 电视剧
     * http://list.iqiyi.com/www/2/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/2/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/2/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/2/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * <p>
     * <p>
     * 生成待爬取页面集合 综艺
     * http://list.iqiyi.com/www/6/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/6/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/6/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/6/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * <p>
     * <p>
     * http://127.0.0.1:8090/iqiyi/filmeUrl
     */
    @RequestMapping("/filmeUrl")
    public String filmeUrl() {

        return "crawler/notice";
    }


    /**
     * 爬取电影、影视、综艺数据并上传至HDFS
     * http://127.0.0.1:8090/iqiyi/filemData
     *
     * @return
     */
    @RequestMapping("/filemData")
    public String filemData() {

        return "crawler/notice";
    }


}


