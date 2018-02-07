package com.hxqh.crawler.test;

import com.hxqh.crawler.util.CrawlerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/1/25.
 *
 * @author Lin
 */
public class BaiduCrawlerTest {


    Integer NUM = 6;

    //    @Test
    public void test() {
        // <电影名称，Url>
        Map<String, String> detailMap = new HashMap<>();
        List<String> urlList = Arrays.asList(
                "https://www.baidu.com/s?wd=%E6%88%98%E7%8B%BC2",
                "https://www.baidu.com/s?wd=%E8%8A%B3%E5%8D%8E");
        for (int i = 0; i < urlList.size(); i++) {
            String string = urlList.get(i);
            try {
                String html = CrawlerUtils.fetchHTMLContent(string, 2);
                Document doc = Jsoup.parse(html);
                for (int j = 1; j <= NUM; j++) {
                    Element element = doc.getElementById(String.valueOf(j));
                    Element element1 = element.select("h3").get(0);
                    Element a = element1.select("a").get(0);
                    String title = a.text();
                    if (title.contains("_百度百科")) {
                        System.out.println(title);
                        String href = element.select("h3")
                                .get(0).select("a").get(0).attr("href");
                        System.out.println(href);
                        detailMap.put(title, href);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 获取电影公司名称
        for (Map.Entry<String, String> entry : detailMap.entrySet()) {
            String name = entry.getKey();
            String url = entry.getValue();
            try {
                String html = CrawlerUtils.fetchHTMLContent(url, 2);
                Document doc = Jsoup.parse(html);
                String keys = doc.getElementsByClass("basicInfo-item name").text();
                String values = doc.getElementsByClass("basicInfo-item value").text();
                System.out.println(keys + " " + values);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //    @Test
    public void urlEncode() {
        String str1 = "%E6%88%98%E7%8B%BC2";
        String str2 = "%E8%8A%B3%E5%8D%8E";
        try {
            String keyWord1 = URLDecoder.decode(str1, "UTF-8");
            System.out.println(keyWord1);
            String keyWord2 = URLDecoder.decode(str2, "UTF-8");
            System.out.println(keyWord2);

            // 将普通字符创转换成application/x-www-from-urlencoded字符串
            String urlString1 = URLEncoder.encode("战狼2", "UTF-8");
            Assert.assertEquals(urlString1, str1);
            String urlString2 = URLEncoder.encode("芳华", "UTF-8");
            Assert.assertEquals(urlString2, str2);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


}