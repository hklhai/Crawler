package com.hxqh.crawler.util;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerDoubanScore;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Ocean lin on 2018/4/12.
 *
 * @author Ocean lin
 */
public class DouBanUtils {


    public static void persistDouBan(String cate, String url, String filmName, CrawlerDoubanSocreRepository crawlerDoubanSocreRepository, String category) {
        try {
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 4);
            Document doc = Jsoup.parse(html);

            Elements elements = doc.getElementsByClass("title");
            Elements ratingElements = doc.getElementsByClass("rating-info");

            Integer count = elements.size() > Constants.DOUBAN_NUM ? Constants.DOUBAN_NUM : elements.size();

            for (int j = 0; j < count; j++) {
                Element element = elements.get(j);

                String str = element.select("h3").text();
                //[电影]  战狼2 可播放
                String[] split = str.split(" ");

                String trim = split[0].trim();
                String searchCategory = trim.substring(1, trim.length() - 1);
                if (trim.equals(cate) || split.length >= 2) {

                    String searchFilmName = split[1].trim();
                    searchFilmName = searchFilmName.substring(1, searchFilmName.length());

                    if (searchCategory.equals(cate) && searchFilmName.equals(filmName)) {
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

                        System.out.println("分类：" + cate + " 电影名称:" + filmName + " 评分：" + aFloat + " 评分人数：" + integer);
                        CrawlerDoubanScore crawlerDoubanScore = new CrawlerDoubanScore(category, filmName, aFloat, integer);
                        crawlerDoubanSocreRepository.save(crawlerDoubanScore);
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
