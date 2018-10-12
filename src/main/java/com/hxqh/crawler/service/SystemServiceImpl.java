package com.hxqh.crawler.service;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.*;
import com.hxqh.crawler.model.*;
import com.hxqh.crawler.repository.UserRepository;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
                    field("playNum", videosFilm.getPlayNum()).
                    field("addTime", todayTime).endObject();

            IndexResponse result = this.client.prepareIndex(Constants.FILM_SOAP_VARIETY_INDEX, Constants.FILM_SOAP_VARIETY_TYPE).setSource(content).get();
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

            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("source", book.getSource()).
                    field("bookName", book.getBookName()).
                    field("category", book.getCategory()).
                    field("categoryLable", book.getCategoryLable()).
                    field("price", book.getPrice()).
                    field("commnetNum", book.getCommnetNum()).
                    field("author", book.getAuthor()).
                    field("publish", book.getPublish()).
                    field("addTime", todayTime).endObject();

            IndexResponse result = this.client.prepareIndex(Constants.BOOK_INDEX, Constants.BOOK_TYPE).setSource(content).get();
            System.out.println(book.getBookName() + " Persist to ES Success!");
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Deprecated
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

    /**
     * curl -H "Content-Type: application/x-ndjson" -XPOST "spark3:9200/film_data/film/_bulk?pretty" --data-binary @film_data.json
     * <p>
     * 如果提示 Result window is too large
     */
    @Override
    public void export() {
        SearchResponse response = client.prepareSearch("market_book")
                .setTypes("book").setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();
        SearchHits resultHits = response.getHits();

        Long totalHits = resultHits.totalHits;
        response = client.prepareSearch("market_book")
                .setTypes("book").setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(totalHits.intValue())
                .execute().actionGet();
        resultHits = response.getHits();

        StringBuilder stringBuilder = new StringBuilder(128214);
        for (int i = 0; i < resultHits.getHits().length; i++) {
            String jsonStr = resultHits.getHits()[i].getSourceAsString();

            String index = "{\"index\":{\"_index\":\"market_book2\",\"_id\":" + i + "}}\n";
            stringBuilder.append(index);
            stringBuilder.append(jsonStr).append("\n");
        }
        String fileName = "d:\\film_data.json";
        FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
    }

    @Override
    public void migrate() {
        //build source settings
        String indexName = "market_analysis";
        SearchResponse scrollResp = client.prepareSearch(indexName)
                .setScroll(new TimeValue(60000)).setSize(1000).execute().actionGet();

        //build destination bulk
        String destiIndexName = "film_data";
        String destiIndexType = "film";
        BulkRequestBuilder bulk = client.prepareBulk();
        // ExecutorService executor = Executors.newFixedThreadPool(5);

        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

        while (true) {
            bulk = client.prepareBulk();
            final BulkRequestBuilder bulk_new = bulk;
            for (SearchHit hit : scrollResp.getHits().getHits()) {

                IndexRequest req = client.prepareIndex().setIndex(destiIndexName)
                        .setType(destiIndexType).setSource(hit.getSourceAsString()).request();
                bulk_new.add(req);
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    bulk_new.execute();
                }
            });

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
    }

    @Deprecated
    @Override
    public ResponseEntity addJdCrawlerBookURLList(List<CrawlerBookURL> crawlerBookURLList) {
        String todayTime = DateUtils.getTodayTime();
        Integer length = null;

        try {
            String index = "market_analysis_book";
            String type = "book";

            Long count = (long) crawlerBookURLList.size();
            //核心方法BulkRequestBuilder拼接多个Json
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (int i = 0; i < count; i++) {
                CrawlerBookURL crawlerBookURL = crawlerBookURLList.get(i);
                XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                        field("url", crawlerBookURL.getUrl()).
                        field("title", crawlerBookURL.getTitle()).
                        field("addTime", crawlerBookURL.getAddTime()).
                        field("category", crawlerBookURL.getCategory()).
                        field("platform", crawlerBookURL.getPlatform()).
                        field("createTime", todayTime).endObject();
                bulkRequest.add(client.prepareIndex(index, type).setSource(content));
            }
            BulkResponse bulkItemResponses = bulkRequest.execute().actionGet();
            length = bulkItemResponses.getItems().length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(length, HttpStatus.OK);


    }

    @Override
    public ResponseEntity addMaoYanList(List<RealTimeMovie> movieArrayList) {
        String todayTime = DateUtils.getTodayTime();
        Integer length = null;

        try {
            Long count = (long) movieArrayList.size();
            //核心方法BulkRequestBuilder拼接多个Json
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (int i = 0; i < count; i++) {
                RealTimeMovie realTimeMovie = movieArrayList.get(i);

                XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                        field("platform", realTimeMovie.getPlatform()).
                        field("filmName", realTimeMovie.getFilmName()).
                        field("boxInfo", realTimeMovie.getBoxInfo()).
                        field("sumBoxInfo", realTimeMovie.getSumBoxInfo()).
                        field("splitBoxInfo", realTimeMovie.getSplitBoxInfo()).
                        field("splitSumBoxInfo", realTimeMovie.getSplitSumBoxInfo()).
                        field("releaseInfo", realTimeMovie.getReleaseInfo()).
                        field("addTime", todayTime).
                        field("showInfo", realTimeMovie.getShowInfo()).endObject();
                bulkRequest.add(client.prepareIndex(Constants.MAOYAN_INDEX, Constants.MAOYAN_TYPE).setSource(content));
            }
            //插入文档至ES, 完成！
            BulkResponse bulkItemResponses = bulkRequest.execute().actionGet();
            length = bulkItemResponses.getItems().length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(length, HttpStatus.OK);
    }

    @Override
    public ResponseEntity addLiterature(Literature literature) {
        String todayTime = DateUtils.getTodayTime();

        try {
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("platform", literature.getPlatform()).
                    field("name", literature.getName()).
                    field("author", literature.getAuthor()).
                    field("mainclass", literature.getMainclass()).
                    field("subclass", literature.getSubclass()).
                    field("label", literature.getLabel()).
                    field("commentnum", literature.getCommentnum()).
                    field("clicknum", literature.getClicknum()).
                    field("addtime", todayTime).endObject();

            IndexResponse result = this.client.prepareIndex(Constants.LITERATURE_INDEX, Constants.LITERATURE_TYPE).setSource(content).get();
            // System.out.println(literature.getName() + " Persist to ES Success!");
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 持久化爱奇艺电影URL
     *
     * @param href    url连接
     * @param urlInfo url信息
     */
    @Override
    public void addFilmOrSoapUrl(String href, URLInfo urlInfo) {
        Document doc = Jsoup.parse(href);
        String title = doc.select("a").get(0).attr("title");
        String url = doc.select("a").get(0).attr("href");
        String addTime = DateUtils.getTodayDate();
        CrawlerURL crawlerURL = new CrawlerURL(title, url, addTime, urlInfo.getCategory(), urlInfo.getPlatform(), urlInfo.getSorted());
        try {
            String todayTime = DateUtils.getTodayTime();
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("addTime", crawlerURL.getAddTime()).
                    field("category", crawlerURL.getCategory()).
                    field("sorted", crawlerURL.getSorted()).
                    field("title", crawlerURL.getTitle()).
                    field("url", crawlerURL.getUrl()).
                    field("platform", crawlerURL.getPlatform()).
                    field("createTime", todayTime).endObject();

            this.client.prepareIndex(Constants.SOAP_URL_INDEX, Constants.SOAP_URL_TYPE).setSource(content).get();
            // System.out.println(crawlerURL.getTitle() + " Persist to ES Success!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveLiterature(CrawlerLiteratureURL literature) {
        String todayTime = DateUtils.getTodayTime();
        try {
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("url", literature.getUrl()).
                    field("title", literature.getTitle()).
                    field("sorted", "").
                    field("platform", literature.getPlatform()).
                    field("addTime", literature.getAddTime()).
                    field("createTime", todayTime).endObject();

            this.client.prepareIndex(Constants.LITERATURE_URL_INDEX, Constants.LITERATURE_URL_TYPE).setSource(content).get();
            // System.out.println(literature.getTitle() + " Persist to ES Success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addVariety(List<CrawlerVariety> list) {
        for (int i = 0; i < list.size(); i++) {
            CrawlerVariety crawlerVariety = list.get(i);
            try {
                String todayTime = DateUtils.getTodayTime();
                XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                        field("addTime", crawlerVariety.getAddTime()).
                        field("sorted", crawlerVariety.getSorted()).
                        field("url", crawlerVariety.getUrl()).
                        field("createTime", todayTime).endObject();
                this.client.prepareIndex(Constants.HISTORY_VARIETY_INDEX, Constants.HISTORY_VARIETY_TYPE).setSource(content).get();
                // System.out.println(crawlerVariety.getUrl() + " Persist to ES Success!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addVarietyURL(List<CrawlerVarietyURL> urlList) {
        for (int i = 0; i < urlList.size(); i++) {
            CrawlerVarietyURL crawlerVariety = urlList.get(i);
            try {
                String todayTime = DateUtils.getTodayTime();
                XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                        field("addTime", crawlerVariety.getAddTime()).
                        field("category", crawlerVariety.getCategory()).
                        field("sorted", crawlerVariety.getSorted()).
                        field("title", crawlerVariety.getTitle()).
                        field("url", crawlerVariety.getUrl()).
                        field("platform", crawlerVariety.getPlatform()).
                        field("varietyname", crawlerVariety.getVarietyName()).
                        field("createTime", todayTime).endObject();

                this.client.prepareIndex(Constants.HISTORY_VARIETY_URL_INDEX, Constants.HISTORY_VARIETY_URL_TYPE).setSource(content).get();
                // System.out.println(crawlerVariety.getTitle() + " Persist to ES Success!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addBookURL(CrawlerBookURL crawlerBookURL) {
        String todayTime = DateUtils.getTodayTime();
        try {
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("url", crawlerBookURL.getUrl()).
                    field("title", crawlerBookURL.getTitle()).
                    field("platform", crawlerBookURL.getPlatform()).
                    field("category", crawlerBookURL.getCategory()).
                    field("addTime", crawlerBookURL.getAddTime()).
                    field("createTime", todayTime).endObject();

            this.client.prepareIndex(Constants.BOOK_URL_INDEX, Constants.BOOK_URL_TYPE).setSource(content).get();
            // System.out.println(crawlerBookURL.getTitle() + " Persist to ES Success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
