package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.VideosFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import static com.hxqh.crawler.common.Constants.BILLION;
import static com.hxqh.crawler.common.Constants.TEN_THOUSAND;

public class PersistTencentFilm implements Runnable {
    private static final Integer STRINGBUILDER_SIZE = 300;


    private List<CrawlerURL> l;
    private CrawlerProblemRepository crawlerProblemRepository;
    private SystemService systemService;


    public PersistTencentFilm(List<CrawlerURL> l, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) {
        this.l = l;
        this.crawlerProblemRepository = crawlerProblemRepository;
        this.systemService = systemService;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(l, crawlerProblemRepository, systemService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void parseAndPersist(List<CrawlerURL> hrefList, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder(STRINGBUILDER_SIZE);
        for (int i = 0; i < hrefList.size(); i++) {
            CrawlerURL crawlerURL = hrefList.get(i);

            String htmls = CrawlerUtils.fetchHTMLAndIframeContent(crawlerURL.getUrl(),  Constants.DEFAULT_SEELP_SECOND);
            String[] html = htmls.split("hxqh");
            Document doc = Jsoup.parse(html[0]);
            Document commentdoc = Jsoup.parse(html[1]);

            String source = crawlerURL.getPlatform();//电影来自源
            String filmName = new String();//电影名字
            String star = new String();//演员
            String director = new String();//导演
            String label ;//类型
            String score ;//评分
            String commentNum ;//评分人数！！
            String up ;//顶
            String addTime = DateUtils.getTodayDate();//添加时间
            String playNum ;//播放量
            String category = crawlerURL.getCategory();//电影种类
            try {
                Elements filmNameElement = doc.getElementsByClass("video_title _video_title");
                if (filmNameElement != null) {
                    String[] filmNames = filmNameElement.text().split(" ");
                    filmName = filmNames[0];
                }
                Elements starElement = doc.getElementsByClass("director").select("a");
                if (starElement != null) {
                    String[] stars = starElement.text().split(" ");
                    for (int j = 0; j < stars.length; j++) {
                        if (j <= 0) {
                            director = stars[0];
                        } else {
                            star += stars[j] + ",";
                        }
                    }
                }
                Elements labelelements = doc.getElementsByClass("video_tags _video_tags").select("a");
                label = labelelements.text().replaceAll(" ", ",");
                Elements scoreelements = doc.getElementsByClass("video_score").select("span");
                score = scoreelements.text().split(" ")[0];
                commentNum = commentdoc.getElementById("J_CommentTotal").text();
                up = doc.getElementsByClass("mood_progress_value _mood_progress").attr("style");
                String[] property = up.split(":");
                up = property[1].substring(1, property[1].length() - 2);
                playNum = doc.getElementById("mod_cover_playnum").text();
                if (playNum.endsWith("万")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double d = Double.valueOf(playNum) * TEN_THOUSAND;
                    playNum = String.valueOf(d.longValue());
                }
                if (playNum.endsWith("亿")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double d = Double.valueOf(playNum) * BILLION;
                    playNum = String.valueOf(d.longValue());
                }


                /**
                 * 解析并持久化至本地文件系统
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
                String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT +
                        DateUtils.getTodayDate() + "-"+crawlerURL.getPlatform();
                FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
                stringBuilder.setLength(0);
                System.out.println(filmName.trim() + " Persist Success!");

                /**
                 * 持久化至ES
                 */
                if (!score.trim().equals("评论人数不足")) {
                    VideosFilm videosFilm=new VideosFilm(source.trim(), filmName.trim(), star.trim(), director.trim(),
                            category.trim(), label.trim(), Float.valueOf(score.trim()), Integer.valueOf(commentNum.trim()),
                            Integer.valueOf(up.trim()), addTime.trim(), Integer.valueOf(playNum.trim()));
                    systemService.addVideos(videosFilm);
                }else{
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                CrawlerUtils.persistProblemURL(crawlerProblemRepository,crawlerURL);
            }
        }
    }
}
