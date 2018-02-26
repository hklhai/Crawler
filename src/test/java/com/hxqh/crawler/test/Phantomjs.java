package com.hxqh.crawler.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ocean lin on 2018/2/26.
 *
 * @author Ocean lin
 */
public class Phantomjs {
    public static void main(String[] args) {
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持（第二参数表明的是你的phantomjs引擎所在的路径）
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "E:\\Program\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);

        //设置隐性等待（作用于全局）
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //打开页面
        driver.get("https://item.jd.com/11672203.html");
        //查找元素

        WebElement webElement = driver.findElement(By.xpath("/html"));
        String outerHTML = webElement.getAttribute("outerHTML");

        Document doc = Jsoup.parse(outerHTML);
        Elements authorElement = doc.getElementById("p-author").select("a");
        String author = authorElement.text();
        System.out.println(author);
    }

}
