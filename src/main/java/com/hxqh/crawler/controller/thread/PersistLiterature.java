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
import org.jsoup.nodes.Element;
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
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(literatureURL.getUrl(), Constants.DEFAULT_SEELP_SECOND_JD_BOOK);
            Document doc = Jsoup.parse(html);

            String platform = literatureURL.getPlatform();
            String name = literatureURL.getTitle();
            String author = new String();
            String mainclass = new String();
            String subclass = new String();
            String label = new String();
            Float scorenum = null;
            Integer commentnum = null;
            Long clicknum = null;

            try {
                Elements authorElement = doc.getElementsByClass("book-info");
                if (authorElement != null) {
                    author = authorElement.get(0).select("h1").select("span").select("a").text();
                    Element clicknumElement = authorElement.select("p").get(2);
                    Element element = clicknumElement.select("em").get(1);
                    Element element1 = clicknumElement.select("cite").get(1);
                    String num = element.text() + element1.text().split("·")[0];
                    if (num.endsWith("万总点击")) {
                        num = num.substring(0, num.length() - 5);
                        if (!num.equals("")) {
                            clicknum = Float.valueOf(num).longValue() * Constants.TEN_THOUSAND;
                        } else {
                            clicknum = Float.valueOf(0).longValue();
                        }
                    }
                    if (num.endsWith("亿总点击")) {
                        num = num.substring(0, num.length() - 5);
                        clicknum = Long.valueOf(num) * Constants.BILLION;
                    }
                }


                Element mainClassElement = doc.getElementsByClass("crumbs-nav center990  top-op").get(0);
                if (mainClassElement != null) {
                    Elements select = mainClassElement.select("span").select("a");
                    mainclass = select.get(1).text();
                    subclass = select.get(2).text();
                }

                Element labelElement = doc.getElementsByClass("tag").get(0);
                if (labelElement != null) {
                    label = labelElement.select("span").text();
                    label = label + " " + labelElement.select("a").text();
                }

                Element score1 = doc.getElementById("score1");
                Element score2 = doc.getElementById("score2");
                if (score1 != null && score2 != null) {
                    String s = score1.text() + "." + score2.text();
                    scorenum = Float.valueOf(s);
                }

                Element commentnumElement = doc.getElementById("J-discusCount");
                if (commentnumElement != null) {
                    String text = commentnumElement.text();
                    text = text.substring(1, text.length() - 2);
                    commentnum = Integer.valueOf(text);
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
                        append(scorenum).append("^").
                        append(commentnum).append("^").
                        append(clicknum).append("^").
                        append(addTime).append("\n");
                String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT +
                        DateUtils.getTodayDate() + "-" + literatureURL.getPlatform();
                FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
                stringBuilder.setLength(0);
                System.out.println(name + " Persist Success!");

                /**
                 * 持久化至ES
                 */
                Literature literature = new Literature(platform, name, author,
                        mainclass, subclass, label, scorenum, commentnum, clicknum);
                systemService.addLiterature(literature);

            } catch (Exception e) {
                System.out.println(literatureURL.getUrl());
                e.printStackTrace();
            }

        }
    }


}
