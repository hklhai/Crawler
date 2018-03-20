package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.VideosFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerVarietyURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ocean lin on 2018/1/19.
 *
 * @author Lin
 */
public class PersistFilm implements Runnable {


    private static final Integer STRINGBUILDER_SIZE = 300;


    private List<CrawlerURL> l;
    private List<CrawlerVarietyURL> varietyURLList;
    private CrawlerProblemRepository crawlerProblemRepository;
    private CrawlerVarietyURLRepository crawlerVarietyURLRepository;
    private SystemService systemService;


    public PersistFilm(List<CrawlerURL> l, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) {
        this.l = l;
        this.crawlerProblemRepository = crawlerProblemRepository;
        this.systemService = systemService;
    }

    public PersistFilm(List<CrawlerVarietyURL> varietyURLList, CrawlerVarietyURLRepository crawlerVarietyURLRepository, SystemService systemService) {
        this.varietyURLList = varietyURLList;
        this.crawlerVarietyURLRepository = crawlerVarietyURLRepository;
        this.systemService = systemService;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(l, varietyURLList, crawlerProblemRepository, systemService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseAndPersist(List<CrawlerURL> hrefList,
                                        List<CrawlerVarietyURL> varietyURLList,
                                        CrawlerProblemRepository crawlerProblemRepository,
                                        SystemService systemService) {
        StringBuilder stringBuilder = new StringBuilder(STRINGBUILDER_SIZE);
        CrawlerURL crawlerURL = null;
        CrawlerVarietyURL varietyURL = null;
        List<Object> objectList = new ArrayList<>();
        Integer len = null;
        if (null != hrefList) {
            len = hrefList.size();
        }
        if (null != varietyURLList) {
            len = varietyURLList.size();
        }


        try {
            for (int i = 0; i < len; i++) {
                String url = new String();
                String source = new String();
                String category = new String();


                if (null != hrefList) {
                    crawlerURL = hrefList.get(i);
                    url = crawlerURL.getUrl();
                    source = crawlerURL.getPlatform();
                    category = crawlerURL.getCategory();

                }
                if (null != varietyURLList) {
                    CrawlerVarietyURL crawlerVarietyURL = varietyURLList.get(i);
                    url = crawlerVarietyURL.getUrl();
                    source = crawlerVarietyURL.getPlatform();
                    category = crawlerVarietyURL.getCategory();
                }


                String html = CrawlerUtils.fetchHTMLContentByPhantomJs(url, Constants.DEFAULT_SEELP_SECOND);
                Document doc = Jsoup.parse(html);

                String filmName = new String();
                String star = new String();
                String director = new String();
                String label = new String();
                String score = new String();

                Element filmNameElement = doc.getElementById("widget-videotitle");
                Elements progInfo = doc.getElementsByClass("progInfo_txt");

                if (filmNameElement != null && progInfo != null) {

                    if (filmNameElement != null) {
                        filmName = filmNameElement.text();
                    }

                    // 电影
                    if (null != hrefList) {
                        // 演员
                        Elements starEle = progInfo.select("p");
                        if (starEle.size() < 3) {
                            System.out.println(url);
                            star = " ";
                        } else {
                            Elements starElement = doc.getElementsByClass("progInfo_txt").
                                    select("p").get(2).select("span").get(1).select("a");
                            if (starElement != null) {
                                star = starElement.text();
                            }
                        }
                    }

                    // 综艺
                    if (null != varietyURLList) {
                        Elements elements = doc.getElementById("datainfo-cast-list").select("a");
                        star = elements.text();
                    }


                    // 导演
                    Elements directorEle = doc.getElementsByClass("progInfo_txt").select("p");
                    if (directorEle.size() < 2) {
                        System.out.println(url);
                        director = " ";
                    } else {
                        Elements directorElement = doc.getElementsByClass("progInfo_txt").
                                select("p").get(1).select("span").get(1).select("a");
                        if (directorElement != null) {
                            director = directorElement.text();
                        }
                    }

                    Element dataInfoElement = doc.getElementById("datainfo-taglist");
                    if (dataInfoElement != null) {
                        Elements labelElement = dataInfoElement.select("a");
                        label = labelElement.text();
                    } else {
                        label = " ";
                    }

                    Element playerAreaScore = doc.getElementById("playerAreaScore");
                    if (null != playerAreaScore) {
                        score = playerAreaScore.attr("snsscore");
                        if ("".equals(score)) {
                            score = "0.0";
                        }
                    } else {
                        score = "0.0";
                    }


                    String commentNum = doc.getElementsByClass("score-user-num").text();
                    if (commentNum.endsWith("万人评分")) {
                        commentNum = commentNum.substring(0, commentNum.length() - 4);
                        Double v = Double.valueOf(commentNum) * Constants.TEN_THOUSAND;
                        commentNum = String.valueOf(v.longValue());
                    }
                    if (commentNum.endsWith("人评分")) {
                        commentNum = commentNum.substring(0, commentNum.length() - 4);
                        if ("".equals(commentNum)) {
                            commentNum = "0";
                        } else {
                            commentNum = String.valueOf(Long.valueOf(commentNum));
                        }
                    }
                    if ("".equals(commentNum)) {
                        commentNum = "0";
                    }

                    String up = doc.getElementById("widget-voteupcount").text();
                    if (up.endsWith("万")) {
                        up = up.substring(0, up.length() - 1);
                        if ("".equals(up)) {
                            up = "0";
                        } else {
                            Double v = Double.valueOf(up) * Constants.TEN_THOUSAND;
                            up = String.valueOf(v.longValue());
                        }
                    }
                    if ("".equals(up)) {
                        up = "0";
                    }

                    String addTime = DateUtils.getTodayDate();
                    String playNum = doc.getElementById("chartTrigger").select("span").text();

                    if (playNum.endsWith("万")) {
                        playNum = playNum.substring(0, playNum.length() - 1);
                        Double v = Double.valueOf(playNum) * Constants.TEN_THOUSAND;
                        playNum = String.valueOf(v.longValue());
                    }
                    if (playNum.endsWith("亿")) {
                        playNum = playNum.substring(0, playNum.length() - 1);
                        Double v = Double.valueOf(playNum) * Constants.BILLION;
                        playNum = String.valueOf(v.longValue());
                    }
                    if ("".equals(playNum)) {
                        playNum = "0";
                    }


                    /**
                     * 3.解析并持久化至本地文件系统
                     */
                    stringBuilder.append(source.trim()).append("^").
                            append(filmName.trim()).append("^").
                            append(star.trim()).append("^").
                            append(director.trim()).append("^").
                            append(category.trim()).append("^").
                            append(label.trim()).append("^").
                            append(score.trim()).append("^").
                            append(commentNum.trim()).append("^").
                            append(up.trim()).append("^").
                            append(addTime.trim()).append("^").
                            append(playNum.trim()).append("\n");


                    if (null != hrefList) {
                        String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT +
                                DateUtils.getTodayDate() + "-" + source;
                        String s = stringBuilder.toString();
                        FileUtils.writeStrToFile(s, fileName);
                        stringBuilder.setLength(0);
                        System.out.println(filmName.trim() + " Persist Success!");

                        /**
                         * 持久化至ES
                         */
                        if (!score.trim().equals("评分人数不足")) {
                            VideosFilm videosFilm = setVideosFilm(source, filmName, star, director, category, label, score, commentNum, up, addTime, playNum);
                            videosFilm.setPlayNum(Integer.valueOf(playNum.trim()));
                            systemService.addVideos(videosFilm);
                        } else {
                            continue;
                        }

                    }
                    if (null != varietyURLList) {

                        String fileName = Constants.SAVE_VARIETY_PATH + Constants.FILE_SPLIT +
                                DateUtils.getTodayDate() + "-" + source;
                        String s = stringBuilder.toString();
                        FileUtils.writeStrToFile(s, fileName);
                        stringBuilder.setLength(0);
                        System.out.println(filmName.trim() + " Persist Success!");

                        /**
                         * 持久化至ES
                         */
                        if (!score.trim().equals("评分人数不足")) {
                            VideosFilm videosFilm = setVideosFilm(source, filmName, star, director, category, label, score, commentNum, up, addTime, playNum);
                            videosFilm.setPlayNum(Integer.valueOf(playNum.trim()));
                            systemService.addVideos(videosFilm);
                        } else {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 持久化无法爬取URL 关闭持久化无法爬取URL
            // CrawlerUtils.persistProblemURL(crawlerProblemRepository, crawlerURL);
        }
    }

    public static VideosFilm setVideosFilm(String source, String filmName, String star, String director, String category, String label, String score, String commentNum, String up, String addTime, String playNum) {
        VideosFilm videosFilm = new VideosFilm();
        videosFilm.setSource(source.trim());
        videosFilm.setFilmName(filmName.trim());
        videosFilm.setStar(star.trim());
        videosFilm.setDirector(director.trim());
        videosFilm.setCategory(category.trim());
        videosFilm.setLabel(label.trim());
        videosFilm.setScoreVal(Float.valueOf(score.trim()));
        commentNum = commentNum.trim();
        if ("评分人数不足".equals(commentNum)) {
            commentNum = "0";
            videosFilm.setCommentNum(Integer.valueOf(commentNum));
        } else {
            videosFilm.setCommentNum(Integer.valueOf(commentNum));
        }
        videosFilm.setUp(Integer.valueOf(up.trim()));
        videosFilm.setAddTime(addTime.trim());
        videosFilm.setPlayNum(Integer.valueOf(playNum));
        return videosFilm;
    }

}
