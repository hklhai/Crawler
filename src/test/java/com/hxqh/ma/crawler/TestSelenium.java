package com.hxqh.ma.crawler;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/1/11.
 */
public class TestSelenium {

    ArrayList<String> hrefList = new ArrayList();


    @Ignore("need chrome driver")
    @Test
    public void test() throws InterruptedException {

        String url = "http://www.iqiyi.com/v_19rrdwvld8.html#vfrm=19-9-0-1";
//        String url = "http://list.iqiyi.com/www/1/-------------11-1-1-iqiyi--.html";
        System.getProperties().setProperty("webdriver.chrome.driver", "E://Program//chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
        Thread.sleep(8000);

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String outerHTML = webElement.getAttribute("outerHTML");
        String[] split = outerHTML.split("\n");
        System.out.println(outerHTML);
//        for (int i = 0; i < split.length; i++) {
//            String href = getHref(split[i]);
//            if (href != null)
//                hrefList.add(href);
//        }
//
//        for (int i = 0; i < hrefList.size(); i++) {
//            if (hrefList.get(i).contains("vfrm=2-4-0-1")) {
//                System.out.println(hrefList.get(i));
//            }
//        }

        webDriver.close();

    }

    /**
     * 从一行字符串中读取链接
     *
     * @return
     */
    private String getHref(String str) {
        Pattern pattern = Pattern.compile("<a .* href=.*</a>");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

}
