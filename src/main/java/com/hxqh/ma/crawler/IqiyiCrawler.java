package com.hxqh.ma.crawler;

import com.hxqh.ma.util.DateUtils;
import com.hxqh.ma.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/1/12.
 */
public class IqiyiCrawler {

    private static final String ENCODE = "charset=.*";

    private static final String A_LABEL = "<a .* href=.*</a>";

    private static final String A_FILM_LABEL = "href=['\"]([^'\"]*)['\"]";

    private static final String START_PAGE = "http://www.iqiyi.com/dianying/";


    private static final String CHROMEDRIVER = "E://Program//chromedriver.exe";
    //private static final String CHROMEDRIVER = "/home/hadoop/app/chromedriver";

    private static final String SAVE_PATH = "E://";
    //private static final String SAVE_PATH = "/home/hadoop/crawler";


    /**
     * 要分析的网页
     */
    String htmlUrl;

    /**
     * 分析结果
     */
    ArrayList<String> hrefList = new ArrayList();

    /**
     * 网页编码方式
     */
    String charSet;

    public IqiyiCrawler(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    /**
     * 获取分析结果
     *
     * @throws IOException
     */
    public ArrayList<String> getHrefList() throws IOException {

        parser();
        return hrefList;
    }

    /**
     * 解析网页链接
     *
     * @return
     * @throws IOException
     */
    private void parser() throws IOException {
        URL url = new URL(htmlUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);

        String contenttype = connection.getContentType();
        charSet = getCharset(contenttype);

        InputStreamReader isr = new InputStreamReader(
                connection.getInputStream(), charSet);
        BufferedReader br = new BufferedReader(isr);

        String str = null, rs = null;
        while ((str = br.readLine()) != null) {
            rs = getHref(str);

            if (rs != null) {
                hrefList.add(rs);
            }
        }

    }

    /**
     * 获取网页编码方式
     *
     * @param str
     */
    private String getCharset(String str) {
        Pattern pattern = Pattern.compile(ENCODE);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0).split("charset=")[1];
        }
        return null;
    }

    /**
     * 从一行字符串中读取链接
     *
     * @return
     */
    private String getHref(String str) {
        Pattern pattern = Pattern.compile(A_LABEL);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private static void parseAndPersist(List<String> hrefList) throws InterruptedException {
        for (int i = 0; i < hrefList.size(); i++) {
            //            WebElement webElement = fetchHTMLContent(hrefList.get(i));
            //            String html = webElement.getAttribute("outerHTML");
            String html = fetchHTMLContent(hrefList.get(i));

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
            StringBuilder stringBuilder = new StringBuilder(150);
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
            String fileName = SAVE_PATH + "\\" + DateUtils.getTodayDate();
            FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
            System.out.println(filmname.trim() + "Persist Success!");
        }
    }

    /**
     * 1.获取待爬取链接
     */
    private static List<String> getFetchLinks() throws IOException {
        IqiyiCrawler a = new IqiyiCrawler(START_PAGE);
        ArrayList<String> hrefList = a.getHrefList();

        List<String> list = new ArrayList<>();
        for (int i = 0; i < hrefList.size(); i++) {
            if (hrefList.get(i).contains("_blank\" rseat") && !(hrefList.get(i).contains("class=\"classes_linkMore\""))) {
                Matcher matcher = Pattern.compile(A_FILM_LABEL).matcher(hrefList.get(i));
                while (matcher.find()) {
                    System.out.println(matcher.group(1));
                    list.add(matcher.group(1));
                }
            }
        }
//        for (String s : hrefList) {
//            System.out.println(s);
//        }

        return list;
    }

    private static String fetchHTMLContent(String url) throws InterruptedException {
        System.getProperties().setProperty("webdriver.chrome.driver", CHROMEDRIVER);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
        Thread.sleep(10000);
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String html = webElement.getAttribute("outerHTML");
        webDriver.quit();
        return html;
    }


    public static void main(String[] arg) throws IOException, InterruptedException {
        // 1.获取待爬取链接
        final List<String> hrefList = getFetchLinks();
//        List<String> hrefList = Arrays.asList("http://www.iqiyi.com/v_19rr7qhp7c.html#vfrm=19-9-0-1");
        parseAndPersist(hrefList);

    }

}
