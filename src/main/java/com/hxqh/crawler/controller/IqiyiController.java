package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerSoapURLRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.repository.CrawlerVarietyURLRepository;
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
    private CrawlerService crawlerService;

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


    @RequestMapping("/varietyUrl")
    public String varietyUrl() throws Exception {



        List<String> hotList = new ArrayList<>();
        List<String> newList = new ArrayList<>();

        List<String> hotUrlList = new ArrayList<>();
        List<String> newUrlList = new ArrayList<>();

        List<CrawlerVariety> hotVarietyUrlList = new ArrayList<>();
        List<CrawlerVariety> newVarietyUrlList = new ArrayList<>();

//        --综艺 热门
//        http://list.iqiyi.com/www/6/-------------11-1-1-iqiyi--.html
//        http://list.iqiyi.com/www/6/-------------11-2-1-iqiyi--.html
//        http://list.iqiyi.com/www/6/-------------11-3-1-iqiyi--.html
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            hotList.add("http://list.iqiyi.com/www/6/-------------11-" + i + "-1-iqiyi--.html");
        }
//        --更新时间
//        http://list.iqiyi.com/www/6/-------------4-1-1-iqiyi--.html
//        http://list.iqiyi.com/www/6/-------------4-2-1-iqiyi--.html
//        http://list.iqiyi.com/www/6/-------------4-3-1-iqiyi--.html
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            newList.add("http://list.iqiyi.com/www/6/-------------4-" + i + "-1-iqiyi--.html");
        }

        /**
         * 获取每部综艺作品链接
         */
        for (String s : hotList) {
            List<String> list = eachVarietyUrlList(s);
            hotUrlList.addAll(list);
        }
        for (String s : newList) {
            List<String> list = eachVarietyUrlList(s);
            newUrlList.addAll(list);
        }

        /**
         * 持久化每部综艺链接
         */
        for (String s : hotUrlList) {
            CrawlerVariety crawlerVariety = new CrawlerVariety(s, DateUtils.getTodayDate(), "hot");
            hotVarietyUrlList.add(crawlerVariety);
        }
        crawlerService.persistEachVarietyUrlList(hotVarietyUrlList);
        for (String s : newUrlList) {
            CrawlerVariety crawlerVariety = new CrawlerVariety(s, DateUtils.getTodayDate(), "new");
            newVarietyUrlList.add(crawlerVariety);
        }
        crawlerService.persistEachVarietyUrlList(newVarietyUrlList);




        return "crawler/notice";
    }


    private List<String> eachVarietyUrlList(String url) {
        List<String> eachVarietyList = new ArrayList<>();
        try {
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 4);
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("site-piclist_pic");
            Elements as = elements.select("a");
            for (Element e : as) {
                System.out.println(e.attr("href"));
                eachVarietyList.add(e.attr("href"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eachVarietyList;
    }


}


