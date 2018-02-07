package com.hxqh.crawler.test;

import com.hxqh.crawler.model.CrawlerDoubanScore;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.util.CrawlerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Lin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DoubanCrawlerTest {

    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;

    Integer NUM = 4;


//    @Test
    public void test() {
        String category = "电影";
//
//        String move1 = "战狼";
//        String move2 = "芳华";
        String urlString1 = null;
//        String urlString2 = null;
        List<CrawlerURL> tencentFilm = crawlerURLRepository.findAll();
        for (int i = 0; i < 3; i++) {
            CrawlerURL crawlerURL = tencentFilm.get(i);
            try {
                urlString1 = URLEncoder.encode(crawlerURL.getTitle(), "UTF-8");
//                urlString2 = URLEncoder.encode(crawlerURL.getTitle(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = "https://www.douban.com/search?q=" + urlString1;
            // <电影名称/图书名称/上映电影，Url>
//            Map<String, String> listlMap = new HashMap<>();
//            List<String> urlList = Arrays.asList(
//                    "https://www.douban.com/search?q=" + urlString1,
//                    "https://www.douban.com/search?q=" + urlString2);

//            for (int j = 0; j < urlList.size(); j++) {
//                String string = urlList.get(j);
            String filmName = null;
            try {
                filmName = URLDecoder.decode(url.split("=")[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                String html = CrawlerUtils.fetchHTMLContent(url, 4);
                Document doc = Jsoup.parse(html);

                Elements elements = doc.getElementsByClass("title");
                Elements ratingElements = doc.getElementsByClass("rating-info");

                for (int j = 0; j < NUM; j++) {
                    Element element = elements.get(j);

                    String str = element.select("h3").text();
                    //[电影]  战狼2 可播放
                    String[] split = str.split(" ");

                    String trim = split[0].trim();
                    String searchCategory = trim.substring(1, trim.length() - 1);
                    if (trim.equals(category) || split.length >= 2) {

                        String searchFilmName = split[1].trim();
                        searchFilmName = searchFilmName.substring(1, searchFilmName.length());


                        if (searchCategory.equals(category) && searchFilmName.equals(filmName)) {
                            Element ratingElement = ratingElements.get(j);
                            String rating = ratingElement.select("span").text();
                            String scoreValue = rating.split(" ")[0];
                            String scoreValuePersonNum = rating.split(" ")[1];
                            scoreValuePersonNum = scoreValuePersonNum.substring(1, scoreValuePersonNum.length() - 4);

                            System.out.println("分类：" + category + " 电影名称:" + filmName + " 评分：" + scoreValue + " 评分人数：" + scoreValuePersonNum);
                            CrawlerDoubanScore crawlerDoubanScore=new CrawlerDoubanScore(category,filmName,scoreValue,scoreValuePersonNum);
                            crawlerDoubanSocreRepository.save(crawlerDoubanScore);
                            break;
                        }
                    }else{
                        System.out.println("没找到");
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            }
        }


    }

}