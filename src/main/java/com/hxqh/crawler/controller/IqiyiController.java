package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


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
    @Autowired
    private CrawlerSoapURLRepository crawlerSoapURLRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerVarietyRepository crawlerVarietyRepository;


    @RequestMapping("/filmUrl")
    public String filmUrl() {


        return "crawler/notice";
    }


    /**
     * 爬取电影、影视、综艺数据并上传至HDFS
     * http://127.0.0.1:8666/iqiyi/filemData
     *
     * @return
     */
    @RequestMapping("/filmData")
    public String filmData() throws Exception {


        return "crawler/notice";
    }


    /**
     * 持久化 一集
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/soapUrl")
    public String soapUrl() throws Exception {
        List<String> hotList = new ArrayList<>();
        List<String> newList = new ArrayList<>();
//        --电视剧 热门
//        http://list.iqiyi.com/www/2/-------------11-1-1-iqiyi--.html
//        http://list.iqiyi.com/www/2/-------------11-2-1-iqiyi--.html
//        http://list.iqiyi.com/www/2/-------------11-3-1-iqiyi--.html
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            hotList.add("http://list.iqiyi.com/www/2/-------------11-" + i + "-1-iqiyi--.html");
        }
//        --更新时间
//        http://list.iqiyi.com/www/2/-------------4-1-1-iqiyi--.html
//        http://list.iqiyi.com/www/2/-------------4-2-1-iqiyi--.html
//        http://list.iqiyi.com/www/2/-------------4-3-1-iqiyi--.html
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            newList.add("http://list.iqiyi.com/www/2/-------------4-" + i + "-1-iqiyi--.html");
        }

        for (String s : hotList) {
            persistUrlList(s, "hot");
        }
        for (String s : newList) {
            persistUrlList(s, "new");
        }

        return "crawler/notice";
    }


    private void persistUrlList(String url, String type) {
        List<CrawlerSoapURL> soapURLList = new ArrayList<>();
        try {
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 2);
            Document document = Jsoup.parse(html);
            Elements select = document.getElementsByClass("site-piclist_pic");
            for (Element e : select) {
                String eachUrl = e.select("a").attr("href");
                String title = e.select("a").attr("title");
                String eachHtml = CrawlerUtils.fetchHTMLContentByPhantomJs(eachUrl, 2);
                Document eachDocument = Jsoup.parse(eachHtml);
                Elements piclist = eachDocument.getElementsByClass("site-piclist_pic_link");
                // 电视剧所有播放量信息均相同，任意取一集即可
                String href = piclist.get(0).attr("href");

                CrawlerSoapURL soapURL = new CrawlerSoapURL(
                        title,
                        href,
                        DateUtils.getTodayDate(),
                        "soap",
                        "iqiyi",
                        type
                );
                soapURLList.add(soapURL);
            }
            crawlerSoapURLRepository.save(soapURLList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/eachVarietyUrl")
    public String eachVarietyUrl() throws Exception {

        List<String> hotList = new ArrayList<>();
        List<String> newList = new ArrayList<>();


        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            hotList.add("http://list.iqiyi.com/www/6/-------------11-" + i + "-1-iqiyi--.html");
        }
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            newList.add("http://list.iqiyi.com/www/6/-------------4-" + i + "-1-iqiyi--.html");
        }

        /**
         * 获取每部综艺作品链接
         */
        for (String s : hotList) {
            List<CrawlerVariety> list = eachVarietyUrlList(s, "hot");
            crawlerService.persistEachVarietyUrlList(list);
        }
        for (String s : newList) {
            List<CrawlerVariety> list = eachVarietyUrlList(s, "new");
            crawlerService.persistEachVarietyUrlList(list);
        }


        return "crawler/notice";
    }


    /**
     * @param url    每部综艺节目爬取URL
     * @param sorted 综艺节目类别
     * @return
     */
    private List<CrawlerVariety> eachVarietyUrlList(String url, String sorted) {
        List<CrawlerVariety> eachVarietyList = new ArrayList<>();
        try {
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 4);
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("site-piclist_pic");
            Elements as = elements.select("a");
            for (Element e : as) {
                CrawlerVariety crawlerVariety = new CrawlerVariety(
                        e.attr("href"),
                        DateUtils.getTodayDate(),
                        sorted);
                eachVarietyList.add(crawlerVariety);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eachVarietyList;
    }


    @RequestMapping("/varietyUrl")
    public String varietyUrl() throws Exception {

        List<CrawlerVariety> varietyList = crawlerVarietyRepository.findAll();
        /**
         * 持久化每部综艺作品的不同集
         */
        for (CrawlerVariety variety : varietyList) {
            String url = variety.getUrl();
            String sorted = variety.getSorted();
            List<CrawlerVarietyURL> urlList = persistVarietyUrlList(url, sorted);
            crawlerService.persistVarietyUrlList(urlList);
        }

        return "crawler/notice";
    }

    /**
     * @param url    每部综艺节目爬取URL
     * @param sorted 综艺节目类别
     * @return
     */
    private List<CrawlerVarietyURL> persistVarietyUrlList(String url, String sorted) {
        List<CrawlerVarietyURL> varietyURLList = new ArrayList<>();
        try {
            varietyURLList = CrawlerUtils.fetchVarietyURLByPhantomJs(url, Constants.IQIYI_VARIETY_WAIT_TIME, sorted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return varietyURLList;
    }

}


