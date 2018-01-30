package com.hxqh.crawler.service;

import com.hxqh.crawler.domain.Book;
import com.hxqh.crawler.domain.VideosFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.User;
import com.hxqh.crawler.repository.UserRepository;
import com.hxqh.crawler.util.DateUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ocean lin on 2017/7/1.
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService {

    @Autowired
    private UserRepository userDao;

    @Autowired
    private TransportClient client;

    @Override
    public User findUserById(String name) {
        return userDao.findUserById(name);
    }

    @Override
    public ResponseEntity addVideos(VideosFilm videosFilm) {
        try {
            String todayTime = DateUtils.getTodayTime();

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
                    field("addTime", todayTime).endObject();

            IndexResponse result = this.client.prepareIndex("market_analysis", "videos").setSource(content).get();
            System.out.println(videosFilm.getFilmName() + " Persist to ES Success!");
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity addBook(Book book) {
        try {
            String todayTime = DateUtils.getTodayTime();

//            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
//                    field("source", book.getSource()).
//                    field("bookName", book.getBookName()).
//                    field("category", book.getCategory()).
//                    field("categoryLable", book.getCategoryLable()).
//                    field("price", book.getPrice()).
//                    field("commnetNum", book.getCommnetNum()).
//                    field("author", book.getAuthor()).
//                    field("publish", book.getPrice()).
//                    field("addTime", todayTime).endObject();
            /**
             * 中文版
             */
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("来源", book.getSource()).
                    field("书名", book.getBookName()).
                    field("分类", book.getCategory()).
                    field("分类标签", book.getCategoryLable()).
                    field("价格", book.getPrice()).
                    field("评论量", book.getCommnetNum()).
                    field("作者", book.getAuthor()).
                    field("出版社", book.getPrice()).
                    field("添加时间", todayTime).endObject();

            IndexResponse result = this.client.prepareIndex("market_book", "book").setSource(content).get();
            System.out.println(book.getBookName() + " Persist to ES Success!");
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity addCrawlerURLList(List<CrawlerURL> crawlerURLList) {
        String todayTime = DateUtils.getTodayTime();
        Integer length = null;

        try {
            String index = "market_analysis_film";
            String type = "film";

            Long count = (long) crawlerURLList.size();
            //核心方法BulkRequestBuilder拼接多个Json
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (int i = 0; i < count; i++) {
                CrawlerURL crawlerURL = crawlerURLList.get(i);
                XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                        field("addTime", crawlerURL.getAddTime()).
                        field("category", crawlerURL.getCategory()).
                        field("sorted", crawlerURL.getSorted()).
                        field("title", crawlerURL.getTitle()).
                        field("url", crawlerURL.getUrl()).
                        field("platform", crawlerURL.getPlatform()).
                        field("createTime", todayTime).endObject();
                bulkRequest.add(client.prepareIndex(index, type).setSource(content));
            }
            //插入文档至ES, 完成！
            BulkResponse bulkItemResponses = bulkRequest.execute().actionGet();
            length = bulkItemResponses.getItems().length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(length, HttpStatus.OK);
    }


}
