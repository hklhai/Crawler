package com.hxqh.ma.util;

import com.hxqh.ma.common.Constants;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/1/16.
 */
public class CrawlerUtils {

    private static final String ENCODE = "charset=.*";

    private static final String A_LABEL = "<a .* href=.*</a>";

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

    public CrawlerUtils(String htmlUrl) {
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


    public static String fetchHTMLContent(String url) throws InterruptedException {
        System.getProperties().setProperty("webdriver.chrome.driver", Constants.CHROMEDRIVER);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
        Thread.sleep(10000);
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String html = webElement.getAttribute("outerHTML");
        webDriver.quit();
        return html;
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

}
