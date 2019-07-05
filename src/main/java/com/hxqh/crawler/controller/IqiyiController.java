package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerSoapURL;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.CrawlerVariety;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.hxqh.crawler.common.Constants.*;


/**
 * @author Ocean Lin
 * <p>
 * Created by Ocean lin on 2017/7/1.
 */
@SuppressWarnings("Duplicates")
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
    public String filmUrl() throws Exception {

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
        // 1. 从数据库获取待爬取链接
        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findFilm();

        List<CrawlerURL> urlList = crawlerURLS.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

        Integer partitionNUm = urlList.size() / Constants.IQIYI_THREAD_NUM + 1;
        List<List<CrawlerURL>> lists = ListUtils.partition(urlList, partitionNUm);
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_THREAD_NUM,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

        for (List<CrawlerURL> l : lists) {
            service.execute(new PersistFilm(l, crawlerProblemRepository, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }

        return "crawler/notice";
    }


    /**
     * 持久化每一集电视剧
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/soapUrl")
    public String soapUrl() {
        List<CrawlerSoapURL> soapURLList = soapURLRepository.findAll();

        List<CrawlerSoapURL> urlList = soapURLList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

        Integer partitionNUm = urlList.size() / Constants.IQIYI_THREAD_NUM + 1;
        List<List<CrawlerSoapURL>> lists = ListUtils.partition(urlList, partitionNUm);

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_THREAD_NUM,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

        for (List<CrawlerSoapURL> l : lists) {
            service.execute(new PersistFilm(l, systemService));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }
        return "crawler/notice";
    }


    /**
     * 每部综艺节目爬取
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/variety")
    public String variety() throws Exception {


        // 需要增加分页 vid
        Sort sort = new Sort(Sort.Direction.DESC, "vid");

        Pageable pageable = new PageRequest(PAGE, SIZE, sort);
        Page<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll(pageable);
        Integer totalPages = varietyURLList.getTotalPages();

        // todo 2w 网络资源不足
        if (totalPages > TOTAL_PAGES) {
            totalPages = TOTAL_PAGES;
        }

        for (int i = 0; i < totalPages; i++) {
            pageable = new PageRequest(i, SIZE, sort);
            Page<CrawlerVarietyURL> batch = crawlerVarietyURLRepository.findAll(pageable);
            List<CrawlerVarietyURL> content = batch.getContent();

            List<CrawlerVarietyURL> urlList = content.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                    -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

            Integer partitionNUm = urlList.size() / Constants.IQIYI_VARIETY_THREAD_NUM + 1;
            List<List<CrawlerVarietyURL>> lists = ListUtils.partition(urlList, partitionNUm);

            ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_VARIETY_THREAD_NUM,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

            for (List<CrawlerVarietyURL> l : lists) {
                service.execute(new PersistFilm(l, crawlerVarietyURLRepository, systemService));
            }
            service.shutdown();
            while (!service.isTerminated()) {
            }
        }


        return "crawler/notice";
    }


    /**
     * 采集每部综艺节目每集url
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/varietyDataUrl")
    public String varietyDataUrl() throws Exception {

        // 需要增加分页 vid
        Sort sort = new Sort(Sort.Direction.DESC, "vid");

        Pageable pageable = new PageRequest(PAGE, SIZE, sort);
        Page<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll(pageable);
        Integer totalPages = varietyURLList.getTotalPages();

        for (int i = 0; i < totalPages; i++) {
            pageable = new PageRequest(i, SIZE, sort);
            Page<CrawlerVarietyURL> batch = crawlerVarietyURLRepository.findAll(pageable);
            List<CrawlerVarietyURL> content = batch.getContent();

            List<CrawlerVarietyURL> urlList = content.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                    -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));

            Integer partitionNUm = urlList.size() / Constants.IQIYI_VARIETY_THREAD_NUM + 1;
            List<List<CrawlerVarietyURL>> lists = ListUtils.partition(urlList, partitionNUm);

            ScheduledExecutorService service = new ScheduledThreadPoolExecutor(Constants.IQIYI_VARIETY_THREAD_NUM,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

            for (List<CrawlerVarietyURL> l : lists) {
                service.execute(new PersistFilm(l, crawlerVarietyURLRepository, systemService));
            }
            service.shutdown();
            while (!service.isTerminated()) {
            }
        }


        return "crawler/notice";
    }

    /**
     * 综艺整体迁移
     */
    @RequestMapping("/varietyMigrate")
    public String varietyMigrate() {
        Sort sort = new Sort(Sort.Direction.ASC, "vid");

        Pageable pageable = new PageRequest(PAGE, SIZE, sort);
        Page<CrawlerVariety> varietyURLList = crawlerVarietyRepository.findAll(pageable);
        Integer totalPages = varietyURLList.getTotalPages();

        for (int i = 0; i < totalPages; i++) {
            pageable = new PageRequest(i, SIZE, sort);
            Page<CrawlerVariety> batch = crawlerVarietyRepository.findAll(pageable);
            List<CrawlerVariety> content = batch.getContent();
            systemService.addVariety(content);
        }
        return "crawler/notice";
    }



    @RequestMapping("/iqiyiVarietyUrlList")
    public String iqiyiVarietyUrlList() throws Exception {
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
        // crawlerService.deleteIqiyiVarietyURL();

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


