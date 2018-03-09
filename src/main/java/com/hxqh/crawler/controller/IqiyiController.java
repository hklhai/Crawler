package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerSoapURLRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
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

}


