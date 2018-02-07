package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.Book;
import com.hxqh.crawler.model.CrawlerBookURL;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
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
 * Created by Ocean lin on 2018/1/29.
 *
 * @author Ocean lin
 */
public class PersistJdBook implements Runnable {
    private static final Integer STRINGBUILDER_SIZE = 300;

    private List<CrawlerBookURL> list;
    private CrawlerProblemRepository crawlerProblemRepository;
    private SystemService systemService;

    public PersistJdBook(List<CrawlerBookURL> list, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) {
        this.list = list;
        this.crawlerProblemRepository = crawlerProblemRepository;
        this.systemService = systemService;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(list, crawlerProblemRepository, systemService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void parseAndPersist(List<CrawlerBookURL> hrefList, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) throws Exception {
        StringBuilder stringBuilder = new StringBuilder(STRINGBUILDER_SIZE);

        for (int i = 0; i < hrefList.size(); i++) {
            CrawlerBookURL crawlerBookURL = hrefList.get(i);
            String html = CrawlerUtils.fetchHTMLContent(crawlerBookURL.getUrl(), Constants.DEFAULT_SEELP_SECOND_JD_BOOK);
            Document doc = Jsoup.parse(html);

            String bookName = new String();
            String source = crawlerBookURL.getPlatform();
            String category = crawlerBookURL.getCategory();
            String categoryLable = new String();
            String price = new String();
            String commnetNum = new String();
            String author = new String();
            String publish = new String();

            try {
                Element bookNameElement = doc.getElementById("name");
                if (bookNameElement != null) {
                    bookName = bookNameElement.select("h1").text();
                }

                Element categoryLableElement = doc.getElementsByClass("breadcrumb").get(0);
                if (categoryLableElement != null) {
                    categoryLable = categoryLableElement.select("span").select("a").text();
                }

                Element priceElement = doc.getElementById("jd-price");
                if (priceElement != null) {
                    price = priceElement.text().substring(1, priceElement.text().length());
                    if (price.equals("无报价")) {
                        price = "0";
                    }
                }

                Element commnetNumElement = doc.getElementById("comment-count").select("a").get(0);
                if (commnetNumElement != null) {
                    commnetNum = commnetNumElement.text().trim();
                    if (commnetNum.endsWith("+")) {
                        commnetNum = commnetNum.substring(0, commnetNum.length() - 1);
                        if (commnetNum.endsWith("万")) {
                            commnetNum = commnetNum.substring(0, commnetNum.length() - 2);
                            Double v = Double.valueOf(commnetNum) * Constants.TEN_THOUSAND;
                            commnetNum = String.valueOf(v.longValue());
                        }
                        if (commnetNum.endsWith("亿")) {
                            commnetNum = commnetNum.substring(0, commnetNum.length() - 2);
                            Double v = Double.valueOf(commnetNum) * Constants.BILLION;
                            commnetNum = String.valueOf(v.longValue());
                        }
                    }
                }

                Elements authorElement = doc.getElementById("p-author").select("a");
                if (authorElement != null) {
                    author = authorElement.text();
                }

                Element publishElement = doc.getElementById("parameter2").select("li").get(0);
                if (publishElement != null) {
                    publish = publishElement.attr("title");
                }
                String addTime = DateUtils.getTodayDate();

                /**
                 * 3.解析并持久化至本地文件系统
                 */
                stringBuilder.append(source.trim()).append("^").
                        append(bookName.trim()).append("^").
                        append(category.trim()).append("^").
                        append(categoryLable.trim()).append("^").
                        append(price.trim()).append("^").
                        append(commnetNum.trim()).append("^").
                        append(author.trim()).append("^").
                        append(publish.trim()).append("^").
                        append(addTime.trim()).append("\n");
                String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT +
                        DateUtils.getTodayDate() + "-" + crawlerBookURL.getPlatform();
                FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
                stringBuilder.setLength(0);
                System.out.println(bookName.trim() + " Persist Success!");

                /**
                 * 持久化至ES
                 */
                Book book = new Book(source.trim(), bookName.trim(), category.trim(), categoryLable.trim(),
                        Float.valueOf(price.trim()), Long.valueOf(commnetNum.trim()), author.trim(),
                        publish.trim());
                systemService.addBook(book);


            } catch (Exception e) {
                e.printStackTrace();
                CrawlerURL crawlerURL = new CrawlerURL(bookName, crawlerBookURL.getUrl(), DateUtils.getTodayDate(),
                        crawlerBookURL.getCategory(), crawlerBookURL.getPlatform(), null);
                // 持久化无法爬取URL
                CrawlerUtils.persistProblemURL(crawlerProblemRepository, crawlerURL);
            }

        }
    }


}
