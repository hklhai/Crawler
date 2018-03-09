package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.model.CrawlerDoubanScore;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.util.CrawlerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */
public class PersistDouBan implements Runnable {

    Integer NUM = 4;


    private List<CrawlerURL> l;
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;

    public PersistDouBan(List<CrawlerURL> l, CrawlerDoubanSocreRepository crawlerDoubanSocreRepository) {
        this.l = l;
        this.crawlerDoubanSocreRepository = crawlerDoubanSocreRepository;
    }

    @Override
    public void run() {
        String category = "电影";
        String urlString1 = null;

        for (int i = 0; i < l.size(); i++) {
            CrawlerURL crawlerURL = l.get(i);
            try {
                urlString1 = URLEncoder.encode(crawlerURL.getTitle(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = "https://www.douban.com/search?q=" + urlString1;
            // <电影名称/图书名称/上映电影，Url>
            String filmName = null;
            try {
                filmName = URLDecoder.decode(url.split("=")[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, 4);
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
                            CrawlerDoubanScore crawlerDoubanScore = new CrawlerDoubanScore(category, filmName, scoreValue, scoreValuePersonNum);
                            crawlerDoubanSocreRepository.save(crawlerDoubanScore);
                            break;
                        }
                    } else {
                        System.out.println("No Info");
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}
