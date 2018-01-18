package com.hxqh.crawler.test;


import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/1/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IqiyiCrawler {

    @Resource
    private CrawlerURLRepository crawlerURLRepository;


    @Test
    public void persist() throws IOException, InterruptedException {
        // 1. 从数据库获取待爬取链接
        List<String> hrefList = new ArrayList<>();

        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findAll();
        for (CrawlerURL crawlerURL : crawlerURLS) {
            hrefList.add(crawlerURL.getUrl());
        }

        List<List<String>> lists = ListUtils.partition(hrefList, 222);

//        List<String> hrefList = Arrays.asList("http://www.iqiyi.com/v_19rr7pgf5g.html#vfrm=2-4-0-1");

        ExecutorService service = Executors.newFixedThreadPool(4);

        for (List<String> l : lists) {
            service.execute(new PersistFilm(l));
        }
        service.shutdown();

        while (!service.isTerminated()) {
        }

    }
}

class PersistFilm implements Runnable {

    private List<String> l;

    public PersistFilm(List<String> l) {
        this.l = l;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void parseAndPersist(List<String> hrefList) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder(150);


        for (int i = 0; i < hrefList.size(); i++) {
            //            WebElement webElement = fetchHTMLContent(hrefList.get(i));
            //            String html = webElement.getAttribute("outerHTML");
            String html = CrawlerUtils.fetchHTMLContent(hrefList.get(i));

            Document doc = Jsoup.parse(html);

            String source = "爱奇艺";
            Element filmnameElement = doc.getElementById("widget-videotitle");
            String filmname = new String();
            if (filmnameElement != null) {
                filmname = filmnameElement.text();
            }

            String star = doc.getElementsByClass("progInfo_txt").
                    select("p").get(2).select("span").get(1).select("a").text();
            String director = doc.getElementsByClass("progInfo_txt").
                    select("p").get(1).select("span").get(1).select("a").text();
            String category = "电影";
            String label = doc.getElementById("datainfo-taglist").select("a").text();
            //String socre = doc.getElementById("playerAreaScore").attr("snsscore");
            String socre = doc.select("span[class=score-new]").get(0).attr("snsscore");
            try {

                String commentnum = doc.getElementsByClass("score-user-num").text();
                if (commentnum.endsWith("万人评分")) {
                    commentnum = commentnum.substring(0, commentnum.length() - 4);
                    commentnum = String.valueOf(Double.valueOf(commentnum) * 10000);
                }
                if (commentnum.endsWith("人评分")) {
                    commentnum = commentnum.substring(0, commentnum.length() - 4);
                    commentnum = String.valueOf(Double.valueOf(commentnum));
                }

                String up = doc.getElementById("widget-voteupcount").text();
                if (up.endsWith("万")) {
                    up = up.substring(0, up.length() - 1);
                    up = String.valueOf(Double.valueOf(up) * 10000);
                }

                String addtime = DateUtils.getTodayDate();
                String playNum = doc.getElementById("chartTrigger").select("span").text();

                if (playNum.endsWith("万")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double v = Double.valueOf(playNum) * 10000;
                    playNum = String.valueOf(v.longValue());
                }
                if (playNum.endsWith("亿")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double v = Double.valueOf(playNum) * 100000000;
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 3.解析并持久化至本地文件系统
             */
            String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT + DateUtils.getTodayDate();
            FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
            System.out.println(filmname.trim() + " Persist Success!");
        }
    }

}
