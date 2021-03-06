package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.Literature;
import com.hxqh.crawler.model.CrawlerLiteratureURL;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by Ocean lin on 2018/3/20.
 *
 * @author Ocean lin
 */
public class PersistLiterature implements Runnable {

    private static final Integer STRINGBUILDER_SIZE = 300;

    private SystemService systemService;
    private List<CrawlerLiteratureURL> list;

    public PersistLiterature(SystemService systemService, List<CrawlerLiteratureURL> list) {
        this.systemService = systemService;
        this.list = list;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(list, systemService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void parseAndPersist(List<CrawlerLiteratureURL> list, SystemService systemService) throws Exception {
        StringBuilder stringBuilder = new StringBuilder(STRINGBUILDER_SIZE);

        for (int i = 0; i < list.size(); i++) {
            CrawlerLiteratureURL literatureURL = list.get(i);
            Document doc = Jsoup.connect(literatureURL.getUrl()).get();

            String html = CrawlerUtils.fetchHTMLContent(literatureURL.getUrl(), Constants.DEFAULT_SEELP_SECOND_17K);
            Document document = Jsoup.parse(html);

            String platform = literatureURL.getPlatform();
            String name = literatureURL.getTitle();
            String author = new String();
            String mainclass = new String();
            String subclass = new String();
            String label = new String();
            Long fans = 0L;
            Integer commentnum = null;
            Long clicknum = null;

            try {

                Elements infoPath = doc.getElementsByClass("infoPath");
                name = infoPath.select("a").get(3).text();
                mainclass = infoPath.select("a").get(1).text();
                subclass = infoPath.select("a").get(2).text();

                author = doc.getElementsByClass("AuthorInfo").select("div").get(0).select("a").get(1).text();
                label = doc.getElementsByClass("label").select("td").select("a").select("span").text();

                String topicCount = document.getElementById("topicCount").text();
                if (topicCount.endsWith("万")) {
                    String substring = topicCount.substring(0, topicCount.length() - 2);
                    if (substring.endsWith(".")) {
                        String substring1 = substring.substring(0, substring.length() - 1);
                        commentnum = Integer.valueOf(substring1) * Constants.TEN_THOUSAND;
                    } else {
                        commentnum = Integer.valueOf(substring) * Constants.TEN_THOUSAND;
                    }
                } else {
                    commentnum = Integer.valueOf(topicCount);
                }
                String howmuchreadBook = document.getElementById("howmuchreadBook").text();
                if (howmuchreadBook.endsWith("万")) {
                    clicknum = Long.valueOf(howmuchreadBook.substring(0, howmuchreadBook.length() - 2)) * Constants.TEN_THOUSAND;
                } else {
                    clicknum = Long.valueOf(howmuchreadBook);
                }

                String addTime = DateUtils.getTodayDate();


                /**
                 * 3.解析并持久化至本地文件系统
                 */
                stringBuilder.append(platform.trim()).append("^").
                        append(name.trim()).append("^").
                        append(author.trim()).append("^").
                        append(mainclass.trim()).append("^").
                        append(subclass.trim()).append("^").
                        append(label.trim()).append("^").
                        append(commentnum).append("^").
//                        append(fans).append("^").
        append(clicknum).append("^").
                        append(addTime).append("\n");
                String fileName = Constants.SAVE_LITERATURE_PATH + Constants.FILE_SPLIT +
                        DateUtils.getTodayDate() + "-" + literatureURL.getPlatform();
                FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
                stringBuilder.setLength(0);
                System.out.println(name + " Persist Success!");

                /**
                 * 持久化至ES
                 */
                Literature literature = new Literature(platform, name, author,
                        mainclass, subclass, label, fans, commentnum, clicknum);
                systemService.addLiterature(literature);

            } catch (Exception e) {
                System.out.println(literatureURL.getUrl());
                e.printStackTrace();
            }

        }
    }


}
