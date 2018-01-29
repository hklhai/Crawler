package com.hxqh.crawler.controller;

import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.repository.CrawlerBookURLRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/1/29.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/jd")
public class JdController {

    @Autowired
    private CrawlerBookURLRepository crawlerBookURLRepository;

    public JdController() {
        super();
    }

    @RequestMapping("/bookUrl")
    public String bookUrl() {

        // 根据其实页面，爬取页面获取Url对应关系
        Map<String, String> map = new HashMap<>();

        String startpage = "http://book.jd.com/booktop/0-0-0.html?category=3287-0-0-0-10001-1#comfort";
        try {
            String outerHTML = CrawlerUtils.fetchHTMLContent(startpage, 6);
            Document doc = Jsoup.parse(outerHTML);
            Element div = doc.getElementsByClass("mc").get(0);
            Elements aLabels = div.select("a");
            Elements elements = div.select("dl").select("dt").select("a");
            for (Element element : aLabels) {
                map.put(element.attr("href"), element.text());
            }
            for (Element element : elements) {
                map.put(element.attr("href"), element.text());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * 样式
         * //book.jd.com/booktop/13627-0-0-0-10001-1.html#comfort:书法
         * //book.jd.com/booktop/20007-0-0-0-10001-1.html#comfort:科技
         *
         * 根据url反向截取，获取前5页每页20条数据url
         */
        for (Map.Entry<String, String> m : map.entrySet()) {
            List<String> urlList = new ArrayList<>();

            // 获取页面中所有url
            String urlPageOne = m.getKey();
            String category = m.getValue();

            // 根据url样式生成前5页url
            String categoryId = (urlPageOne.split("-"))[0].split("/")[4];

            String prefix = "http://book.jd.com/booktop/0-0-0.html?category=" + categoryId + "2-0-0-0-10001-";
            String subfix = "#comfort";
            for (int i = 1; i < 6; i++) {
                String url = prefix + i + subfix;
                urlList.add(url);
            }
            // 爬取页面中所有的url对应的页面中每本书的信息
            for (String url : urlList) {
                List<CrawlerBookURL> crawlerURLList = new ArrayList<>();
                try {

                    // 解析url获取其中的每本的信息
                    String outerHTML = CrawlerUtils.fetchHTMLContent(url, 6);
                    Document doc = Jsoup.parse(outerHTML);
//                    Elements elements = doc.getElementsByClass("m m-list").select("div").select("ul").select("li");

                    Elements elements = doc.getElementsByClass("p-detail");
                    for (Element e : elements) {
                        Elements a = e.select("a");
                        CrawlerBookURL crawlerBookURL = new CrawlerBookURL(
                                "http:" + a.attr("href"),
                                a.attr("title"),
                                DateUtils.getTodayDate(),
                                category, "jd");
                        crawlerURLList.add(crawlerBookURL);
                    }
                    // 完成持久化
                    crawlerBookURLRepository.save(crawlerURLList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "crawler/notice";
    }


    @RequestMapping("/filemData")
    public String filemData() {

        return "crawler/notice";
    }


}
