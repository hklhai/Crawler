package com.hxqh.crawler.util;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerProblem;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/1/16.
 */
public class CrawlerUtils {

    private static final String ENCODE = "charset=.*";

    private static final String A_LABEL = "<a .* href=.*</a>";
    private static final String S_LABEL = "<strong class=.*</strong>";
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


    public static String fetchHTMLContentByPhantomJs(String url, Integer second) throws Exception {
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
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, Constants.PHANTOMJS_PATH);
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);

        Integer sleepTime = second * 1000;
        //设置隐性等待（作用于全局）
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //打开页面
        driver.get(url);
        Thread.sleep(sleepTime);

        //查找元素
        WebElement webElement = driver.findElement(By.xpath("/html"));
        String html = new String();
        if (driver != null) {
            html = webElement.getAttribute("outerHTML");
        }

        return html;
    }

    public static String fetchHTMLContent(String url, Integer second) throws Exception {
//        Integer sleepTime = second * 1000;
//        System.getProperties().setProperty("webdriver.chrome.driver", Constants.CHROMEDRIVER);
//        WebDriver webDriver = new ChromeDriver();
//        webDriver.get(url);
//        Thread.sleep(sleepTime);
//        WebElement webElement = webDriver.findElement(By.xpath("/html"));
//        String html = new String();
//        if (webElement != null) {
//            html = webElement.getAttribute("outerHTML");
//        }
//        webDriver.quit();

        Integer sleepTime = second * 1000;
        System.getProperties().setProperty("webdriver.chrome.driver", Constants.CHROMEDRIVER);
        ChromeDriverService service = new ChromeDriverService
                .Builder().usingDriverExecutable(new File(Constants.CHROMEDRIVER)).usingAnyFreePort().build();
        service.start();
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
        Thread.sleep(sleepTime);
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String html = new String();
        if (webElement != null) {
            html = webElement.getAttribute("outerHTML");
        }
        // 关闭 ChromeDriver 接口
        webDriver.quit();
        service.stop();
        return html;
    }

    public static String fetchHTMLAndIframeContent(String url, Integer second) throws InterruptedException {
        Integer sleepTime = second * 1000;
        System.getProperties().setProperty("webdriver.chrome.driver", Constants.CHROMEDRIVER);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
        Thread.sleep(sleepTime);
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String html = new String();
        if (webElement != null) {
            html = webElement.getAttribute("outerHTML");
        }
        ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0,1600)");
        Thread.sleep(sleepTime);
        WebDriver commentIframe = webDriver.switchTo().frame("commentIframe");
        WebElement element = commentIframe.findElement(By.xpath("/html"));
        String outerHTML = element.getAttribute("outerHTML");
        webDriver.quit();
        return html + "hxqh" + outerHTML;

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
    public static String getHref(String str) {
        Pattern pattern = Pattern.compile(A_LABEL);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static String TencentgetHref(String s) {
        Pattern pattern = Pattern.compile(S_LABEL);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }


    public static void persistProblemURL(CrawlerProblemRepository crawlerProblemRepository, CrawlerURL crawlerURL) {
        CrawlerProblem crawlerProblem = crawlerProblemRepository.findByUrl(crawlerURL.getUrl());
        if (crawlerProblem != null) {
            crawlerProblem.setSuccess(crawlerProblem.getSuccess() + 1);
            crawlerProblemRepository.save(crawlerProblem);
        } else {
            crawlerProblem = new CrawlerProblem(crawlerURL.getUrl(), DateUtils.getTodayDate(),
                    0, crawlerURL.getCategory(), crawlerURL.getPlatform(), crawlerURL.getSorted());
            crawlerProblemRepository.save(crawlerProblem);
        }
    }

    public static void persistCrawlerURL(Map<String, URLInfo> hrefMap, CrawlerURLRepository crawlerURLRepository) {
        List<CrawlerURL> crawlerURLS = new ArrayList<>();
        for (Map.Entry<String, URLInfo> entry : hrefMap.entrySet()) {
            String html = entry.getKey();
            URLInfo urlInfo = entry.getValue();
            Document doc = Jsoup.parse(html);
            String title = doc.select("a").get(0).attr("title").toString();
            String url = doc.select("a").get(0).attr("href").toString();
            String addTime = DateUtils.getTodayDate();
            CrawlerURL crawlerURL = new CrawlerURL(title, url, addTime, urlInfo.getCategory(), urlInfo.getPlatform(), urlInfo.getSorted());
            crawlerURLS.add(crawlerURL);
        }
        crawlerURLRepository.save(crawlerURLS);
    }

}
