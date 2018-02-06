package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistTencentFilm;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.HdfsUtils;
import com.hxqh.crawler.util.HostUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hxqh.crawler.common.Constants.DEFAULT_SEELP_SECOND;
import static com.hxqh.crawler.common.Constants.TENCENT_PAGE_END_NUM;
import static com.hxqh.crawler.common.Constants.TENCENT_PAGE_START_NUM;

@Controller
@RequestMapping("/tencent")
public class TencentController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    Integer TENCENT_PAGE_SIZE = 0;


    @RequestMapping("/filmeUrl")
    public String filmeUrl() {


        /**
         * 取爬取列表前先将数据写入ES
         */
        List<CrawlerURL> crawlerURLList = crawlerURLRepository.findTencentFilm();
        ResponseEntity responseEntity = systemService.addCrawlerURLList(crawlerURLList);

        /**
         * 清除所有mysql数据
         */
        if (responseEntity.getStatusCodeValue() > 0) {
            crawlerURLRepository.delTencentFilm();
        }

        /**
         * 爬取数据
         */
        // 1.所有待爬取URLList
        Map<String, URLInfo> allStartUrlMap = new HashMap<>();
        Map<String, String> prefixSuffixMap = new HashMap<>();
        Map<String, URLInfo> hrefMap = new HashMap<>();
        String prefix = "https://v.qq.com/x/list/movie";
        prefixSuffixMap.put(prefix, "?sort=16&offset=|tencent|film|score");
        prefixSuffixMap.put(prefix, "?sort=18&offset=|tencent|film|hot");
        prefixSuffixMap.put(prefix, "?sort=19&offset=|tencent|film|new");
        //数据说明及赋值完成，即将第一次进入循环
        for (Map.Entry<String, String> entry : prefixSuffixMap.entrySet()) {
            String[] split = entry.getValue().split("\\|");
            String suffix = split[0];
            String platform = split[1];
            String category = split[2];
            String sorted = split[3];

            URLInfo urlInfo = new URLInfo(platform, category, sorted);

            for (int i = TENCENT_PAGE_START_NUM; i < TENCENT_PAGE_END_NUM; i++) {
                if (i > 0) {
                    //大于零的结果
                    String url = prefix + suffix + (TENCENT_PAGE_SIZE += 30);
                    allStartUrlMap.put(url, urlInfo);
                } else {
                    //i小于等于零的结果
                    String url = prefix + suffix + i;
                    allStartUrlMap.put(url, urlInfo);
                }
            }
        }


        for (Map.Entry<String, URLInfo> entry : allStartUrlMap.entrySet()) {
            String url = entry.getKey();
            URLInfo urlInfo = entry.getValue();
            try {
                String outerHTML = CrawlerUtils.fetchHTMLContent(url, DEFAULT_SEELP_SECOND);
                String[] urlsplit = outerHTML.split("\n");
                for (int i = 0; i < urlsplit.length; i++) {
                    String href = CrawlerUtils.TencentgetHref(urlsplit[i]);
                    if (href != null && href.contains("v.qq.com/x/cover")) {
                        hrefMap.put(href, urlInfo);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CrawlerUtils.persistCrawlerURL(hrefMap, crawlerURLRepository);







        return "crawler/notice";
    }



    @RequestMapping("/filemData")
    public String filemData() {

//        try {
//            if (HostUtils.getHostName().equals(Constants.HOST_SPARK4)) {

                // 1. 从数据库获取待爬取链接
//                List<CrawlerURL> crawlerURLS = crawlerURLRepository.findTencentFilm();
//
//
//                List<List<CrawlerURL>> lists = ListUtils.partition(crawlerURLS, Constants.TENCENT_PARTITION_NUM);
//
//                ExecutorService service = Executors.newFixedThreadPool(Constants.TENCENT_THREAD_NUM);
//
//                for (List<CrawlerURL> l : lists) {
//                    service.execute(new PersistTencentFilm(l, crawlerProblemRepository, systemService));
//                }
//                service.shutdown();
//                while (!service.isTerminated()) {
//                }
//
//                // 2. 上传至HDFS
//                try {
//                    HdfsUtils.persistToHDFS("-tencent", Constants.FILE_LOC);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }






        return "crawler/notice";
    }
}
