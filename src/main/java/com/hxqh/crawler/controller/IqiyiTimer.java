package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * spark4 执行
 *
 * @author Ocean Lin
 * Created by Ocean lin on 2017/7/9.
 */
@Component
public class IqiyiTimer {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private CrawlerService crawlerService;

    /**
     * 1. 获取爬取列表前先将数据写入ES
     * 2. 清除所有mysql数据
     * 3. 进行爬取
     */
    // 每个星期日9点00分
    @Scheduled(cron = "0 0 7 ? * SUN")
    public void iqiyiUrlList() {

        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {
                /**
                 * 取爬取列表前先将数据写入ES
                 */
                List<CrawlerURL> crawlerURLList = crawlerURLRepository.findFilm();
                ResponseEntity responseEntity = systemService.addCrawlerURLList(crawlerURLList);

                /**
                 * 清除所有mysql数据
                 */
                if (responseEntity.getStatusCodeValue() > 0) {
                    crawlerService.deleteIqiyiFilm();
                }

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
                //        prefixSuffixMap.put("http://list.iqiyi.com/www/2/-------------11-", "-1-iqiyi--.html|iqiyi|soap|hot");
                //        prefixSuffixMap.put("http://list.iqiyi.com/www/2/-------------4-", "-1-iqiyi--.html|iqiyi|soap|new");
                //        prefixSuffixMap.put("http://list.iqiyi.com/www/6/-------------11-", "-1-iqiyi--.html|iqiyi|variety|hot");
                //        prefixSuffixMap.put("http://list.iqiyi.com/www/6/-------------4-", "-1-iqiyi--.html|iqiyi|variety|new");

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
                        String outerHTML = CrawlerUtils.fetchHTMLContent(url, Constants.DEFAULT_SEELP_SECOND);

                        String[] split = outerHTML.split("\n");
                        for (int i = 0; i < split.length; i++) {
                            String href = CrawlerUtils.getHref(split[i]);
                            if (href != null && href.contains("vfrm=2-4-0-1")) {
                                hrefMap.put(href, urlInfo);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                CrawlerUtils.persistCrawlerURL(hrefMap, crawlerURLRepository);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 0 1 * * ?")
    public void iqiyi() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK3)) {

                // 1. 从数据库获取待爬取链接
                List<CrawlerURL> crawlerURLS = crawlerURLRepository.findFilm();
                Integer partitionNUm = crawlerURLS.size() / Constants.IQIYI_THREAD_NUM + 1;
                List<List<CrawlerURL>> lists = ListUtils.partition(crawlerURLS, partitionNUm);

                ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

                for (List<CrawlerURL> l : lists) {
                    service.execute(new PersistFilm(l, crawlerProblemRepository, systemService));
                }
                service.shutdown();
                while (!service.isTerminated()) {
                }

                // 2. 上传至HDFS
                try {
                    HdfsUtils.persistToHDFS("-iqiyi", Constants.FILE_LOC);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
