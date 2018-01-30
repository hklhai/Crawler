package com.hxqh.crawler.service;

import com.hxqh.crawler.domain.Book;
import com.hxqh.crawler.domain.VideosFilm;
import com.hxqh.crawler.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by Ocean lin on 2017/7/1.
 */
@Service("systemService")
public interface SystemService {

    User findUserById(String name);

    ResponseEntity addVideos(VideosFilm videosFilm);

    ResponseEntity addBook(Book book);
}
