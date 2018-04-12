package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.VDouBanCrawlerBook;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.util.DouBanUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */
public class PersistDouBanBook implements Runnable {

    private List<VDouBanCrawlerBook> l;
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;
    private String category;

    public PersistDouBanBook(List<VDouBanCrawlerBook> l, CrawlerDoubanSocreRepository crawlerDoubanSocreRepository, String category) {
        this.l = l;
        this.crawlerDoubanSocreRepository = crawlerDoubanSocreRepository;
        this.category = category;
    }

    @Override
    public void run() {

        String cate = category == "book" ? "书籍" : "";

        String urlString1 = null;

        for (int i = 0; i < l.size(); i++) {
            VDouBanCrawlerBook crawlerURL = l.get(i);
            try {
                urlString1 = URLEncoder.encode(crawlerURL.getTitle(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url =  Constants.DOUBAN_SEARCH_URL + urlString1;
            // <电影名称/图书名称/上映电影，Url>
            String filmName = null;
            try {
                filmName = URLDecoder.decode(url.split("=")[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            DouBanUtils.persistDouBan(cate, url, filmName, crawlerDoubanSocreRepository, category);
        }


    }


}
