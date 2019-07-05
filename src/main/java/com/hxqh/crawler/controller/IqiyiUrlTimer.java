package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;
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

import java.io.IOException;
import java.net.URISyntaxException;
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
     * <p>
     * 每月15号8点
     */
    // @Scheduled(cron = "0 0 8 15 * ?")
    public void iqiyiFilmUrlList() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK1)) {

                /**
                 * 爬取数据
                 */
                // 1.所有待爬取URLList
                Map<String, URLInfo> allStartURLMap = new HashMap<>();
                Map<String, String> prefixSuffixMap = new HashMap<>();
                Map<String, URLInfo> hrefMap = new HashMap<>();

                // 2.获取全部页面Url
                prefixSuffixMap.put("http://list.iqiyi.com/www/1/-------------11-", "-1-iqiyi--.html|iqiyi|film|hot");
                prefixSuffixMap.put("http://list.iqiyi.com/www/1/-------------4-", "-1-iqiyi--.html|iqiyi|film|new");
                prefixSuffixMap.put("http://list.iqiyi.com/www/1/-------------8-", "-1-iqiyi--.html|iqiyi|film|score");

                for (Map.Entry<String, String> entry : prefixSuffixMap.entrySet()) {
                    String prefix = entry.getKey();
                    String[] split = entry.getValue().split("\\|");
                    String suffix = split[0];
                    String platform = split[1];
                    String category = split[2];
                    String sorted = split[3];

                    URLInfo urlInfo = new URLInfo(platform, category, sorted);

                    for (int i = Constants.PAGE_START_NUM; i <= Constants.PAGE_END_NUM; i++) {
                        String url = prefix + i + suffix;
                        allStartURLMap.put(url, urlInfo);
                    }
                }

                for (Map.Entry<String, URLInfo> entry : allStartURLMap.entrySet()) {
                    String url = entry.getKey();
                    URLInfo urlInfo = entry.getValue();
                    try {
                        String outerHTML = CrawlerUtils.fetchHTMLContentByPhantomJs(url, Constants.DEFAULT_SEELP_SECOND);

                        String[] split = outerHTML.split("\n");
                        for (int i = 0; i < split.length; i++) {
                            String href = CrawlerUtils.getHref(split[i]);
                            if (href != null && href.contains("vfrm=2-4-0-1")) {
                                // 写入ElasticSerach
                                systemService.addFilmOrSoapUrl(href, urlInfo);
                                hrefMap.put(href, urlInfo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                crawlerService.persistFilmUrl(hrefMap);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 每月15号14点
     */
    // @Scheduled(cron = "0 0 14 15 * ?")
    public void iqiyiSoapUrlList() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    /**
     * 1. 先爬取节目链接
     * 2. 再爬取每个链接对应节目URl
     * <p>
     * 每月15号10点
     */
    //@Scheduled(cron = "0 0 10 15 * ?")
    public void iqiyiVarietyUrlList() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
                /****************************** 爬取链接 ************************************/

                List<String> hotList = new ArrayList<>();
                List<String> newList = new ArrayList<>();

                for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
                    hotList.add("http://list.iqiyi.com/www/6/-------------11-" + i + "-1-iqiyi--.html");
                }
                for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
                    newList.add("http://list.iqiyi.com/www/6/-------------4-" + i + "-1-iqiyi--.html");
                }

                crawlerService.deleteIqiyiVariety();
                /**
                 * 获取每部综艺作品链接
                 */
                for (String s : hotList) {
                    List<CrawlerVariety> list = eachVarietyUrlList(s, "hot");
                    crawlerService.persistEachVarietyUrlList(list);
                    systemService.addVariety(list);
                }
                for (String s : newList) {
                    List<CrawlerVariety> list = eachVarietyUrlList(s, "new");
                    crawlerService.persistEachVarietyUrlList(list);
                    systemService.addVariety(list);
                }
                /****************************** 爬取链接 ************************************/


                /****************************  爬取综艺节目 ********************************/
                List<CrawlerVariety> varietyList = crawlerVarietyRepository.findAll();

                // 清除综艺url
                crawlerService.deleteIqiyiVarietyURL();

                /**
                 * 持久化每部综艺作品的不同集
                 */
                for (CrawlerVariety variety : varietyList) {
                    String url = variety.getUrl();
                    String sorted = variety.getSorted();
                    List<CrawlerVarietyURL> urlList = persistVarietyUrlList(url, sorted);
                    crawlerService.persistVarietyUrlList(urlList);
                    // 持久化至ElasticSearch
                    systemService.addVarietyURL(urlList);
                }
                /****************************  爬取综艺节目 ********************************/
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
