package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.User;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2017/7/1.
 */
@Controller
@RequestMapping("/system")
public class SystemController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    /**
     * 页面跳转接口
     * http://127.0.0.1:8090/system/user
     *
     * @return
     */
    @RequestMapping("/user")
    public String user() {
        return "user/index";
    }

    /**
     * 数据接口
     * http://127.0.0.1:8090/system/userData?name=xdm
     *
     * @param name 用户名
     * @return
     */
    @RequestMapping("userData")
    @ResponseBody
    public User userData(@RequestParam(value = "name") String name) {
        return systemService.findUserById(name);
    }


    /**
     * 生成待爬取页面集合 电影
     * <p>
     * http://list.iqiyi.com/www/1/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/1/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/1/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/1/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * http://list.iqiyi.com/www/1/-------------8-1-1-iqiyi--.html  评分
     * http://list.iqiyi.com/www/1/-------------8-2-1-iqiyi--.html  评分
     * <p>
     * <p>
     * <p>
     * 生成待爬取页面集合 电视剧
     * http://list.iqiyi.com/www/2/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/2/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/2/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/2/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * <p>
     * <p>
     * 生成待爬取页面集合 综艺
     * http://list.iqiyi.com/www/6/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/6/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/6/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/6/-------------4-2-1-iqiyi--.html  更新时间
     *
     */
    @RequestMapping("/iqiyiurl")
    public String iqiyiurl() {
        // 所有待爬取URLList
        Map<String, URLInfo> allStartURLMap = new HashMap<>();
        Map<String, String> prefixSuffixMap = new HashMap<>();
        Map<String, URLInfo> hrefMap = new HashMap<>();

        // 1.获取全部页面Url
        prefixSuffixMap.put("http://list.iqiyi.com/www/1/-------------11-", "-1-iqiyi--.html|iqiyi|film|hot");
        prefixSuffixMap.put("http://list.iqiyi.com/www/1/-------------4-", "-1-iqiyi--.html|iqiyi|film|new");
        prefixSuffixMap.put("http://list.iqiyi.com/www/1/-------------8-", "-1-iqiyi--.html|iqiyi|film|score");
        prefixSuffixMap.put("http://list.iqiyi.com/www/2/-------------11-", "-1-iqiyi--.html|iqiyi|soap|hot");
        prefixSuffixMap.put("http://list.iqiyi.com/www/2/-------------4-", "-1-iqiyi--.html|iqiyi|soap|new");
        prefixSuffixMap.put("http://list.iqiyi.com/www/6/-------------11-", "-1-iqiyi--.html|iqiyi|variety|hot");
        prefixSuffixMap.put("http://list.iqiyi.com/www/6/-------------4-", "-1-iqiyi--.html|iqiyi|variety|new");

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
                String outerHTML = CrawlerUtils.fetchHTMLContent(url);

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

        // 3.解析
        List<CrawlerURL> crawlerURLS = new ArrayList<>();
        // . 取每个页面的需要持久化URL
        for (Map.Entry<String, URLInfo> entry : hrefMap.entrySet()) {
            String html = entry.getKey();
            URLInfo urlInfo = entry.getValue();
            Document doc = Jsoup.parse(html);
            String title = doc.select("a").get(0).attr("title").toString();
            String url = doc.select("a").get(0).attr("href").toString();
            String addTime = DateUtils.getTodayDate();

            CrawlerURL crawlerURL =
                    new CrawlerURL(title, url, addTime, urlInfo.getCategory(), urlInfo.getPlatform(), urlInfo.getSorted());
            crawlerURLS.add(crawlerURL);
        }
        crawlerURLRepository.save(crawlerURLS);

        return "crawler/notice";
    }


//    /**
//     * 爬取数据并上传至HDFS
//     * http://127.0.0.1:8090/system/iqiyi
//     *
//     * @return
//     */
//    @RequestMapping("/iqiyi")
//    public String iqiyiFilm() {
//        iqiyiCrawler();
//
//        // 上传至HSDF
//        try {
//            persistToHDFS();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "crawler/notice";
//    }


}


