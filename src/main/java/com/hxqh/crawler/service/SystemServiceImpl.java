package com.hxqh.crawler.service;

import com.hxqh.crawler.domain.VideosFilm;
import com.hxqh.crawler.model.User;
import com.hxqh.crawler.repository.UserRepository;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Ocean lin on 2017/7/1.
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService {

    @Autowired
    private UserRepository userDao;

    @Autowired
    private TransportClient client;

    public User findUserById(String name) {
        return userDao.findUserById(name);
    }


    public ResponseEntity addVideos(VideosFilm videosFilm) {
        try {
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("source", videosFilm.getSource()).
                    field("filmName", videosFilm.getFilmName()).
                    field("star", videosFilm.getStar()).
                    field("director", videosFilm.getDirector()).
                    field("category", videosFilm.getCategory()).
                    field("label", videosFilm.getLabel()).
                    field("scoreVal", videosFilm.getScoreVal()).
                    field("commentNum", videosFilm.getCommentNum()).
                    field("up", videosFilm.getUp()).
                    field("addTime", new Date()).endObject();

            IndexResponse result = this.client.prepareIndex("market_analysis", "videos").setSource(content)
                    .get();
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
