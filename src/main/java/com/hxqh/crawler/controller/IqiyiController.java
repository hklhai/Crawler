package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.HdfsUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    @Autowired
    private CrawlerSoapURLRepository soapURLRepository;


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
     * 持久化每一集电视剧
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/soapUrl")
    public String soapUrl() throws Exception {
        List<String> hotList = new ArrayList<>();
        List<String> newList = new ArrayList<>();
//        --电视剧 热门
        for (int i = Constants.PAGE_START_NUM; i < Constants.PAGE_END_NUM; i++) {
            hotList.add("http://list.iqiyi.com/www/2/-------------11-" + i + "-1-iqiyi--.html");
        }
//        --更新时间
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


    /**
     * @param url    每部电视剧URL
     * @param sorted 电视剧类别
     * @return
     */
    private void persistUrlList(String url, String sorted) {
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
            crawlerSoapURLRepository.save(soapURLList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 持久化电视剧内容
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/soapDataUrl")
    public String soapDataUrl() throws Exception {
        List<CrawlerSoapURL> soapURLList = soapURLRepository.findAll();

        Integer partitionNUm = soapURLList.size() / Constants.IQIYI_THREAD_NUM + 1;
        List<List<CrawlerSoapURL>> lists = ListUtils.partition(soapURLList, partitionNUm);

        ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

        for (List<CrawlerSoapURL> l : lists) {
            service.execute(new PersistFilm(l, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }

        // 2. 上传至HDFS
        try {
            HdfsUtils.persistToHDFS("-soap-iqiyi", Constants.FILE_LOC_SOAP);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "crawler/notice";
    }


    /**
     * 每部综艺节目链接爬取
     *
     * @return
     * @throws Exception
     */
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


    /**
     * 每部综艺节目每集爬取
     *
     * @return
     * @throws Exception
     */
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


    /**
     * 持久化综艺内容
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/varietyDataUrl")
    public String varietyDataUrl() throws Exception {
        List<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll();

        Integer partitionNUm = varietyURLList.size() / Constants.IQIYI_THREAD_NUM + 1;
        List<List<CrawlerVarietyURL>> lists = ListUtils.partition(varietyURLList, partitionNUm);

        ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

        for (List<CrawlerVarietyURL> l : lists) {
            service.execute(new PersistFilm(l, crawlerVarietyURLRepository, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }

        // 2. 上传至HDFS
        try {
            HdfsUtils.persistToHDFS("-variety-iqiyi", Constants.FILE_LOC_VARIETY);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "crawler/notice";
    }

}


