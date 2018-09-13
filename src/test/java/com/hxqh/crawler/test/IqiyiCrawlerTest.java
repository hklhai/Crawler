package com.hxqh.crawler.test;


import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.*;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by Ocean lin on 2018/1/18.
 *
 * @author Lin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IqiyiCrawlerTest {

    @Resource
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerVarietyRepository crawlerVarietyRepository;
    @Autowired
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    @Autowired
    private CrawlerSoapURLRepository soapURLRepository;


    @Test
    public void persist() {
////        List<CrawlerSoapURL> soapURLList = soapURLRepository.findAll();
//        List<CrawlerURL> hrefList = crawlerURLRepository.findFilm();
////        List<CrawlerVarietyURL> varietyURLList = crawlerVarietyURLRepository.findAll();
//
//        StringBuilder stringBuilder = new StringBuilder(300);
//        CrawlerURL crawlerURL = null;
//        Integer len = null;
//        if (null != hrefList) {
//            len = hrefList.size();
//        }
////        if (null != varietyURLList) {
////            len = varietyURLList.size();
////        }
////        if (null != soapURLList) {
////            len = soapURLList.size();
////        }
//
//
//        try {
//            for (int i = 0; i < len; i++) {
//                String url = new String();
//                String source = new String();
//                String category = new String();
//                String commentNum = new String();
//
//
//                if (null != hrefList) {
//                    crawlerURL = hrefList.get(i);
//                    url = crawlerURL.getUrl();
//                    source = crawlerURL.getPlatform();
//                    category = crawlerURL.getCategory();
//
//                }
////                if (null != varietyURLList) {
////                    CrawlerVarietyURL crawlerVarietyURL = varietyURLList.get(i);
////                    url = crawlerVarietyURL.getUrl();
////                    source = crawlerVarietyURL.getPlatform();
////                    category = crawlerVarietyURL.getCategory();
////                }
////                if (null != soapURLList) {
////                    CrawlerSoapURL crawlerSoapURL = soapURLList.get(i);
////                    url = crawlerSoapURL.getUrl();
////                    source = crawlerSoapURL.getPlatform();
////                    category = crawlerSoapURL.getCategory();
////                }
//
//
//                String html = CrawlerUtils.fetchHTMLContent(url, Constants.DEFAULT_SEELP_SECOND_IQIYI);
//                Document doc = Jsoup.parse(html);
//
//                String filmName = new String();
//                String star = new String();
//                String director = new String();
//                String label = new String();
//                String score = new String();
//
//                // 名称
//                if (!"variety".equals(category)) {
//                    filmName = doc.getElementsByClass("header-link").text();
//                } else {
//                    filmName = doc.getElementsByClass("header-link").text();
//                    Elements player_title = doc.getElementsByClass("player-title");
//                    if (player_title.size() > 0) {
//                        filmName += filmName + " : " + player_title.get(0).select("em").text();
//                    }
//                }
//                Elements elements = null;
//                Elements intro_detail_elements = doc.getElementsByClass("intro-detail");
//                if (intro_detail_elements.size() > 0) {
//                    elements = intro_detail_elements.get(0).getElementsByClass("intro-detail-item");
//                } else {
//                    continue;
//                }
//
//                // 演员
//                if (!"variety".equals(category)) {
//                    star = elements.get(2).select("a").text();
//                } else {
//                    star = elements.get(0).select("a").text();
//                }
//                // 导演
//                director = elements.get(1).select("a").text();
//
//                // label
//                label = doc.getElementsByClass("qy-player-tag").get(0).select("a").text();
//                if ("film".equals(category)) {
//                    // 分数 score
//                    Elements span = doc.getElementsByClass("score-new").select("span");
//                    if (span.size() > 0) {
//                        score = span.get(0).text();
//                    } else {
//                        score = "0.0";
//                    }
//                    // 评分人数
//                    commentNum = doc.getElementsByClass("score-user-num").text();
//                    if (commentNum.endsWith("万人评分")) {
//                        commentNum = commentNum.substring(0, commentNum.length() - 4);
//                        Double v = Double.valueOf(commentNum) * Constants.TEN_THOUSAND;
//                        commentNum = String.valueOf(v.longValue());
//                    }
//                    if (commentNum.endsWith("人评分")) {
//                        commentNum = commentNum.substring(0, commentNum.length() - 4);
//                        if ("".equals(commentNum)) {
//                            commentNum = "0";
//                        } else {
//                            commentNum = String.valueOf(Long.valueOf(commentNum));
//                        }
//                    }
//                    if ("".equals(commentNum)) {
//                        commentNum = "0";
//                    }
//
//                } else if (!"film".equals(category)) {
//                    score = "0.0";
//                    commentNum = "0";
//                }
//                String up = new String();
//                Elements func_item = doc.getElementsByClass("func-item func-like");
//                if (func_item.size() > 0) {
//                    Elements span = func_item.get(0).select("span");
//                    if (span.size() > 0) {
//                        up = span.get(0).text();
//                    } else {
//                        up = "0";
//                    }
//                } else {
//                    up = "0";
//                }
//                if (up.endsWith("万")) {
//                    up = up.substring(0, up.length() - 1);
//                    if ("".equals(up)) {
//                        up = "0";
//                    } else {
//                        Double v = Double.valueOf(up) * Constants.TEN_THOUSAND;
//                        up = String.valueOf(v.longValue());
//                    }
//                }
//                if ("".equals(up)) {
//                    up = "0";
//                }
//
//
//                String addTime = DateUtils.getTodayDate();
//                String playNum = doc.getElementById("titleRow").getElementsByClass("basic-txt").get(0).text();
//                if ("影片简介".equals(playNum) || "剧集简介".equals(playNum) || "栏目简介".equals(playNum)) {
//                    html = CrawlerUtils.fetchHTMLContent(url, Constants.MORE_SEELP_SECOND_IQIYI);
//                    doc = Jsoup.parse(html);
//                    playNum = doc.getElementById("titleRow").getElementsByClass("basic-txt").get(0).text();
//                    if ("影片简介".equals(playNum) || "剧集简介".equals(playNum) || "栏目简介".equals(playNum)) {
//                        playNum = "0";
//                    }
//                }
//
//
//                if (playNum.endsWith("万")) {
//                    playNum = playNum.substring(0, playNum.length() - 1);
//                    Double v = Double.valueOf(playNum) * Constants.TEN_THOUSAND;
//                    playNum = String.valueOf(v.longValue());
//                }
//                if (playNum.endsWith("亿")) {
//                    playNum = playNum.substring(0, playNum.length() - 1);
//                    Double v = Double.valueOf(playNum) * Constants.BILLION;
//                    playNum = String.valueOf(v.longValue());
//                }
//                if ("".equals(playNum)) {
//                    playNum = "0";
//                }
//
//
//                /**
//                 * 3.解析并持久化至本地文件系统
//                 */
//                stringBuilder.append(source.trim()).append("^").
//                        append(filmName.trim()).append("^").
//                        append(star.trim()).append("^").
//                        append(director.trim()).append("^").
//                        append(category.trim()).append("^").
//                        append(label.trim()).append("^").
//                        append(score.trim()).append("^").
//                        append(commentNum.trim()).append("^").
//                        append(up.trim()).append("^").
//                        append(addTime.trim()).append("^").
//                        append(playNum.trim()).append("\n");
//
//                if (null != hrefList) {
//                    String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT +
//                            DateUtils.getTodayDate() + "-" + source;
//                    String s = stringBuilder.toString();
//                    FileUtils.writeStrToFile(s, fileName);
//                    stringBuilder.setLength(0);
//                    System.out.println(filmName.trim() + " Persist Success!");
//                }
////                if (null != varietyURLList) {
////
////                    String fileName = Constants.SAVE_VARIETY_PATH + Constants.FILE_SPLIT +
////                            DateUtils.getTodayDate() + "-" + source;
////                    String s = stringBuilder.toString();
////                    FileUtils.writeStrToFile(s, fileName);
////                    stringBuilder.setLength(0);
////                    System.out.println(filmName.trim() + " Persist Success!");
////                }
////                if (null != soapURLList) {
////                    String fileName = Constants.SAVE_SOAP_PATH + Constants.FILE_SPLIT +
////                            DateUtils.getTodayDate() + "-" + source;
////                    String s = stringBuilder.toString();
////                    FileUtils.writeStrToFile(s, fileName);
////                    stringBuilder.setLength(0);
////                    System.out.println(filmName.trim() + " Persist Success!");
////                }
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 持久化无法爬取URL 关闭持久化无法爬取URL
//            // CrawlerUtils.persistProblemURL(crawlerProblemRepository, crawlerURL);
//        }

    }


}

