package com.hxqh.crawler.test;

import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Ocean lin on 2018/2/8.
 *
 * @author Lin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaiduNuoMiCrawlerTest {

    @Test
    public void baiduNouMi() {
        String url = "http://piaofang.baidu.com/";
        try {
            String htmlContent = CrawlerUtils.fetchHTMLContent(url, 3);
            Document doc = Jsoup.parse(htmlContent);
            Elements elementsByClass = doc.getElementsByClass("detail-list");
            Elements elements = elementsByClass.select("dd");
            StringBuilder stringBuilder = new StringBuilder(5000);
            for (int i = 2; i < elements.size(); i++) {
                Element h3 = elements.get(i).select("h3").get(0);

                stringBuilder.append(DateUtils.getTodayTime()).append("^");

                stringBuilder.append(h3.select("b").get(0).text()).append("^");
                stringBuilder.append(h3.select("span").get(0).text()).append("^");
                stringBuilder.append(h3.select("span").get(0).attr("data-box-office")).append("^");

                Element element = elements.get(i).select("div").get(0);
                stringBuilder.append(element.select("span").get(0).text())
                        .append("^")
                        .append(element.select("span").get(1).text())
                        .append("^")
                        .append(element.select("span").get(2).text())
                        .append("^")
                        .append(element.select("span").get(3).text())
                        .append("^")
                        .append(element.select("span").get(4).text())
                        .append("^")
                        .append(element.select("span").get(5).text() + "\n");
            }
            System.out.println(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}