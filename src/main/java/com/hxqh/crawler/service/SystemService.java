package com.hxqh.crawler.service;

import com.hxqh.crawler.domain.*;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ocean lin on 2017/7/1.
 *
 * @author Lin
 */
@Service("systemService")
public interface SystemService {

    User findUserById(String name);

    ResponseEntity addVideos(VideosFilm videosFilm);

    ResponseEntity addBook(Book book);

    ResponseEntity addCrawlerURLList(List<CrawlerURL> crawlerURLList);

    void export();

    void migrate();

    ResponseEntity addJdCrawlerBookURLList(List<CrawlerBookURL> crawlerBookURLList);

    ResponseEntity addMaoYanList(List<RealTimeMovie> movieArrayList);

    ResponseEntity addLiterature(Literature literature);

    void addFilmOrSoapUrl(String href, URLInfo urlInfo);

    void saveLiterature(CrawlerLiteratureURL literatureURL);
}
