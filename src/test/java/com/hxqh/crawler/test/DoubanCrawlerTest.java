package com.hxqh.crawler.test;

import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.NumUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Lin
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DoubanCrawlerTest {

    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;

    @Autowired
    private CrawlerUtils crawlerUtils;

    Integer NUM = 4;


//    @Test
    public void test() {
        String url = "https://www.douban.com/search?q=%E6%88%98%E7%8B%BC";
        // <电影名称/图书名称/上映电影，Url>
        String filmName = null;
        try {
            filmName = URLDecoder.decode(url.split("=")[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            String html = crawlerUtils.fetchHTMLContentByPhantomJsUseProxy(url, 4);
            Document doc = Jsoup.parse(html);

            Elements elements = doc.getElementsByClass("title");
            Elements ratingElements = doc.getElementsByClass("rating-info");

            Integer count = elements.size() > NUM ? NUM : elements.size();

            for (int j = 0; j < count; j++) {
                Element element = elements.get(j);

                String str = element.select("h3").text();
                //[电影]  战狼2 可播放
                String[] split = str.split(" ");

                String trim = split[0].trim();
                String searchCategory = trim.substring(1, trim.length() - 1);
                if (trim.equals("电影") || split.length >= 2) {

                    String searchFilmName = split[1].trim();
                    searchFilmName = searchFilmName.substring(1, searchFilmName.length());

                    if (searchCategory.equals("电影") && searchFilmName.equals(filmName)) {
                        Float aFloat = null;
                        Integer integer = null;
                        Element ratingElement = ratingElements.get(j);
                        String rating = ratingElement.select("span").text();
                        String scoreValue = rating.split(" ")[0];
                        String scoreValuePersonNum = rating.split(" ")[1];
                        scoreValuePersonNum = scoreValuePersonNum.substring(1, scoreValuePersonNum.length() - 4);
                        if (NumUtils.isDouble(scoreValue)) {
                            aFloat = Float.valueOf(scoreValue);
                        } else {
                            continue;
                        }
                        if (NumUtils.isInteger(scoreValuePersonNum)) {
                            integer = Integer.valueOf(scoreValuePersonNum);
                        } else {
                            continue;
                        }

                        System.out.println("分类：" + "电影" + " 电影名称:" + filmName + " 评分：" + aFloat + " 评分人数：" + integer);
                        break;
                    }
                } else {
                    System.out.println("No Info");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}