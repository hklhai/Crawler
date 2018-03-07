package com.hxqh.crawler.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hxqh.crawler.util.CrawlerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

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
                String html = CrawlerUtils.fetchHTMLContentByPhantomJs(string, 2);
                Document doc = Jsoup.parse(html);
                for (int j = 1; j <= NUM; j++) {
                    Element element = doc.getElementById(String.valueOf(j));
                    Element element1 = element.select("h3").get(0);
                    Element a = element1.select("a").get(0);
                    String title = a.text();
                    if (title.contains("_百度百科")) {
                        String href = element.select("h3")
                                .get(0).select("a").get(0).attr("href");
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
                String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 2);
                Document doc = Jsoup.parse(html);
                Elements keys = doc.getElementsByClass("basicInfo-item name");
                Elements values = doc.getElementsByClass("basicInfo-item value");

                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < keys.size(); i++) {
                    Element keyEle = keys.get(i);
                    Element valuesEle = values.get(i);
                    map.put(keyEle.text(), valuesEle.text());
                }
                for (Map.Entry<String, String> e : map.entrySet()) {
                    System.out.println(e.getKey() + ":" + e.getValue());
                }
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(map));
                String result = jsonObject.toString();
                System.out.println(result);
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