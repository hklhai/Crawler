package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistLiterature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.repository.CrawlerLiteratureURLRepository;
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
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/17k")
public class K17Controller {

    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerLiteratureURLRepository crawlerLiteratureURLRepository;


    /**
     * 爬取起点网络文学URL
     *
     * @return
     */
    @RequestMapping("/literatureUrl")
    public String literatureUrl() {
        List<String> list = new ArrayList<>();

        // 爬取10页
        for (int i = 1; i <= 10; i++) {
            String s = "http://all.17k.com/lib/book/2_0_0_0_0_0_0_0_";
            list.add(s + i + ".html");
        }

        for (int i = 0; i < list.size(); i++) {
            List<CrawlerLiteratureURL> crawlerLiteratureURLList = new ArrayList<>();
            String url = list.get(i);
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 5);
            Document doc = Jsoup.parse(html);
            Elements jts = doc.getElementsByClass("jt");
            for (int j = 1; j < jts.size(); j++) {
                System.out.println(jts.get(j).attr("href") + " " + jts.get(j).text());

                CrawlerLiteratureURL literatureURL = new CrawlerLiteratureURL(jts.get(j).text(),
                        jts.get(j).attr("href"),
                        DateUtils.getTodayDate(),
                        "17k",
                        ""
                );
                crawlerLiteratureURLList.add(literatureURL);
                // 持久化值ElasticSearch
                systemService.saveLiterature(literatureURL);
            }
            // 持久化至MYSQL
            crawlerLiteratureURLRepository.save(crawlerLiteratureURLList);
        }

        return "crawler/notice";
    }


    /**
     * 爬取起点网络文学数据
     *
     * @return
     */
    @RequestMapping("/literatureDataUrl")
    public String literatureDataUrl() {

        List<CrawlerLiteratureURL> varietyURLList = crawlerLiteratureURLRepository.findAll();
        Integer partitionNUm = varietyURLList.size() / Constants.QIDIAN_THREAD_NUM + 1;
        List<List<CrawlerLiteratureURL>> lists = ListUtils.partition(varietyURLList, partitionNUm);

        ExecutorService service = Executors.newFixedThreadPool(Constants.IQIYI_THREAD_NUM);

        for (List<CrawlerLiteratureURL> list : lists) {
            service.execute(new PersistLiterature(systemService, list));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }

        // 2. 上传至HDFS
        try {
            HdfsUtils.persistToHDFS("-literature-qidian", Constants.FILE_LOC);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "crawler/notice";
    }

}
