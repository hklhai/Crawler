package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.repository.CrawlerSoapURLRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.repository.CrawlerVarietyRepository;
import com.hxqh.crawler.repository.CrawlerVarietyURLRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HostUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 仅执行爬取URL操作
 *
 * @author Ocean Lin
 * <p>
 * Created by Ocean lin on 2017/7/9.
 */
@Component
public class IqiyiUrlTimer {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerSoapURLRepository crawlerSoapURLRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerVarietyRepository crawlerVarietyRepository;
    @Autowired
    private CrawlerSoapURLRepository soapURLRepository;


    /**
     * 1. 获取爬取列表前先将数据写入ES
     * 2. 清除所有mysql数据
     * 3. 进行爬取
     */
    // 每月最后一日的上午10:15触发
    @Scheduled(cron = "0 15 10 15 * ?")
    public void iqiyiFilmUrlList() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 每月最后一日的上午10:15触发
    @Scheduled(cron = "0 15 10 15 * ?")
    public void iqiyiSoapUrlList() {
        List<CrawlerSoapURL> all = new ArrayList<>();
        List<String> hotList = new ArrayList<>();
        List<String> newList = new ArrayList<>();
        // 电视剧 热门
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            hotList.add("http://list.iqiyi.com/www/2/-------------11-" + i + "-1-iqiyi--.html");
        }
        // 更新时间
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            newList.add("http://list.iqiyi.com/www/2/-------------4-" + i + "-1-iqiyi--.html");
        }

        for (String s : hotList) {
            List<CrawlerSoapURL> soapURLList = persistUrlList(s, "hot");
            all.addAll(soapURLList);

        }
        for (String s : newList) {
            List<CrawlerSoapURL> soapURLList = persistUrlList(s, "new");
            all.addAll(soapURLList);
        }
        crawlerService.saveSoap(all);

    }


    /**
     * @param url    每部电视剧URL
     * @param sorted 电视剧类别
     * @return
     */
    private List<CrawlerSoapURL> persistUrlList(String url, String sorted) {
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
                        sorted
                );

                soapURLList.add(soapURL);
            }
            return soapURLList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 每月最后一日的上午10:15触发
    @Scheduled(cron = "0 15 10 15 * ?")
    public void iqiyiVarietyUrlList() {

    }

}
