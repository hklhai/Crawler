package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerProblem;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by Ocean lin on 2018/1/19.
 */
public class PersistFilm implements Runnable {

    private static final Integer TEN_THOUSAND = 10000;
    private static final Integer BILLION = 100000000;
    private static final Integer SB_SIZE = 300;


    private List<String> l;
    private CrawlerProblemRepository crawlerProblemRepository;

    public PersistFilm(List<String> l, CrawlerProblemRepository crawlerProblemRepository) {
        this.l = l;
        this.crawlerProblemRepository = crawlerProblemRepository;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(l, crawlerProblemRepository);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void parseAndPersist(List<String> hrefList, CrawlerProblemRepository crawlerProblemRepository) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder(SB_SIZE);


        for (int i = 0; i < hrefList.size(); i++) {
            //            WebElement webElement = fetchHTMLContent(hrefList.get(i));
            //            String html = webElement.getAttribute("outerHTML");
            String url = hrefList.get(i);
            String html = CrawlerUtils.fetchHTMLContent(url);
            Document doc = Jsoup.parse(html);
            String source = "爱奇艺";

            String filmname = new String();
            String star = new String();
            String director = new String();

            try {
                Element filmnameElement = doc.getElementById("widget-videotitle");
                if (filmnameElement != null) {
                    filmname = filmnameElement.text();
                }

                Elements starElement = doc.getElementsByClass("progInfo_txt").
                        select("p").get(2).select("span").get(1).select("a");
                if (starElement != null) {
                    star = starElement.text();
                }


                Elements directorElement = doc.getElementsByClass("progInfo_txt").
                        select("p").get(1).select("span").get(1).select("a");
                if (directorElement != null) {
                    director = directorElement.text();
                }


                String category = "电影";

                Elements labelElement = doc.getElementById("datainfo-taglist").select("a");
                String label = labelElement.text();
                String socre = doc.select("span[class=score-new]").get(0).attr("snsscore");


                String commentnum = doc.getElementsByClass("score-user-num").text();
                if (commentnum.endsWith("万人评分")) {
                    commentnum = commentnum.substring(0, commentnum.length() - 4);
                    commentnum = String.valueOf(Double.valueOf(commentnum) * TEN_THOUSAND);
                }
                if (commentnum.endsWith("人评分")) {
                    commentnum = commentnum.substring(0, commentnum.length() - 4);
                    commentnum = String.valueOf(Double.valueOf(commentnum));
                }

                String up = doc.getElementById("widget-voteupcount").text();
                if (up.endsWith("万")) {
                    up = up.substring(0, up.length() - 1);
                    up = String.valueOf(Double.valueOf(up) * TEN_THOUSAND);
                }

                String addtime = DateUtils.getTodayDate();
                String playNum = doc.getElementById("chartTrigger").select("span").text();

                if (playNum.endsWith("万")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double v = Double.valueOf(playNum) * TEN_THOUSAND;
                    playNum = String.valueOf(v.longValue());
                }
                if (playNum.endsWith("亿")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double v = Double.valueOf(playNum) * BILLION;
                    playNum = String.valueOf(v.longValue());
                }

                stringBuilder.append(source.trim()).append("^").
                        append(filmname.trim()).append("^").
                        append(star.trim()).append("^").
                        append(director.trim()).append("^").
                        append(category.trim()).append("^").
                        append(label.trim()).append("^").
                        append(socre.trim()).append("^").
                        append(commentnum.trim()).append("^").
                        append(up.trim()).append("^").
                        append(addtime.trim()).append("^").
                        append(playNum.trim()).append("\n");
                /**
                 * 3.解析并持久化至本地文件系统
                 */
                String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT + DateUtils.getTodayDate();
                FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
                stringBuilder.setLength(0);
                System.out.println(filmname.trim() + " Persist Success!");
            } catch (Exception e) {
                e.printStackTrace();
                // 持久化无法爬取URL
                CrawlerProblem crawlerProblem = new CrawlerProblem(url, null, DateUtils.getTodayDate(), 0);
                crawlerProblemRepository.save(crawlerProblem);

            }

        }
    }

}
