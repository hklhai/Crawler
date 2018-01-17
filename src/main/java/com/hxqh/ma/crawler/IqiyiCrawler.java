package com.hxqh.ma.crawler;

import com.hxqh.ma.common.Constants;
import com.hxqh.ma.util.CrawlerUtils;
import com.hxqh.ma.util.DateUtils;
import com.hxqh.ma.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/1/12.
 */
public class IqiyiCrawler {

    private static final String A_FILM_LABEL = "<a .*href=.* target=\"_blank\">";

    private static final String START_PAGE = "http://www.iqiyi.com/dianying/";
    private static final Integer PAGE_NUM = 30;


    private static final String A_ATTR_1 = "class=\"site-piclist_pic_link\" target=\"_blank\"";

    private static void parseAndPersist(List<String> hrefList) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder(150);


        for (int i = 0; i < hrefList.size(); i++) {
            //            WebElement webElement = fetchHTMLContent(hrefList.get(i));
            //            String html = webElement.getAttribute("outerHTML");
            String html = CrawlerUtils.fetchHTMLContent(hrefList.get(i));

            Document doc = Jsoup.parse(html);

            String source = "爱奇艺";
            String filmname = doc.getElementById("widget-videotitle").text();
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

    /**
     * 1.获取待爬取链接
     */
    private static List<String> getFetchLinks() throws IOException {
        List<String> list = new ArrayList<>();

        String prefix = "http://list.iqiyi.com/www/1/----------0---11-1-";
        String suffix = "-iqiyi--.html";

        List<String> urlList = new ArrayList<>();

        for (int i = 1; i <= PAGE_NUM; i++) {
            String url = prefix + i + suffix;
            urlList.add(url);
        }

//        for (String s : urlList) {
        CrawlerUtils a = new CrawlerUtils("http://list.iqiyi.com/www/1/----------0---11-1-27-iqiyi--.html");
        ArrayList<String> hrefList = a.getHrefList();
        // http://www.iqiyi.com/v_19rrhokw94.html#vfrm=2-4-0-1
        for (int i = 0; i < hrefList.size(); i++) {
            //if (hrefList.get(i).contains(A_ATTR_1) && !(hrefList.get(i).contains("class=\"classes_linkMore\""))) {
            Matcher matcher = Pattern.compile(A_FILM_LABEL).matcher(hrefList.get(i));
            while (matcher.find()) {
                System.out.println(matcher.group(1));
                list.add(matcher.group(1));
            }
            //}
        }
        for (String string : hrefList) {
            System.out.println(string);
        }
//        }
        return list;
    }


    public static void main(String[] arg) throws IOException, InterruptedException {
        // 1.获取待爬取链接
        final List<String> hrefList = getFetchLinks();
//        List<String> hrefList = Arrays.asList("http://www.iqiyi.com/v_19rr7qhp7c.html#vfrm=19-9-0-1");
        parseAndPersist(hrefList);
    }

}
