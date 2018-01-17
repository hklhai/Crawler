package com.hxqh.ma.crawler;

import com.hxqh.ma.util.CrawlerUtils;
import com.hxqh.ma.util.DateUtils;
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
public class JDCrawler {

    private static final String ENCODE = "charset=.*";

    private static final String A_LABEL = "<a .* href=.*</a>";

    private static final String A_FILM_LABEL = "href=['\"]([^'\"]*)['\"]";

    private static final String START_PAGE = "https://channel.jd.com/1713-3264.html";


    private static void parseAndPersist(List<String> hrefList) throws InterruptedException {

        for (int i = 0; i < hrefList.size(); i++) {


            String html = CrawlerUtils.fetchHTMLContent(hrefList.get(i));

            Document doc = Jsoup.parse(html);

            String source = "京东";
            String bookName = doc.getElementById("name").select("h1").text();
            String category = doc.getElementsByClass("breadcrumb").
                    select("p").get(2).select("span").get(1).select("a").text();
//
//            String categoryLable = doc.getElementById("widget-videotitle").text();
            String price = doc.getElementById("jd-price").text();
            price = price.substring(1, price.length());
            String commnetNum = doc.getElementsByClass("hl_blue hide").text();
            commnetNum = commnetNum.substring(1, commnetNum.length() - 2);
            String author = doc.getElementById("p-author").select("a").text();
            String publish = doc.getElementById("parameter2").select("li").get(0).select("a").text();

            String addtime = DateUtils.getTodayDate();
            System.out.println(addtime);
//            StringBuilder stringBuilder = new StringBuilder(150);
//            stringBuilder.append(source.trim()).append("^").
//                    append(filmname.trim()).append("^").
//                    append(star.trim()).append("^").
//                    append(director.trim()).append("^").
//                    append(category.trim()).append("^").
//                    append(label.trim()).append("^").
//                    append(socre.trim()).append("^").
//                    append(commentnum.trim()).append("^").
//                    append(up.trim()).append("^").
//                    append(addtime.trim()).append("^").
//                    append(playNum.trim()).append("\n");
//
//            /**
//             * 3.解析并持久化至本地文件系统
//             */
//            String fileName = Constants.SAVE_PATH + "\\" + DateUtils.getTodayDate();
//            FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
//            System.out.println(filmname.trim() + "Persist Success!");
        }
    }

    /**
     * 1.获取待爬取链接
     */
    private static List<String> getFetchLinks() throws IOException {
        CrawlerUtils a = new CrawlerUtils(START_PAGE);
        ArrayList<String> hrefList = a.getHrefList();

        List<String> list = new ArrayList<>();
        for (int i = 0; i < hrefList.size(); i++) {
            if (hrefList.get(i).contains("item.jd.com")) {
                //&& !(hrefList.get(i).contains("class=\"classes_linkMore\""))
                Matcher matcher = Pattern.compile(A_FILM_LABEL).matcher(hrefList.get(i));
                while (matcher.find()) {
                    String string = "https:" + matcher.group(1);
//                    System.out.println(string);
                    list.add(string);
                }
            }
        }
//        for (String s : hrefList) {
//            System.out.println(s);
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
