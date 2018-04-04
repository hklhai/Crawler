package com.hxqh.crawler.util;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.URLInfo;
import com.hxqh.crawler.model.CrawlerProblem;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.CrawlerVarietyURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class CrawlerUtils {

    @Autowired
    private ProxyUtils proxyUtils;

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


    public CrawlerUtils() {
        super();
    }


    public String fetchHTMLContentByPhantomJsUseProxy(String url, Integer second) {

        String html = new String();
        PhantomJSDriver driver = null;

        try {
            Proxy proxy = new Proxy();
            String ipAndPort = proxyUtils.getProxyIpAndPort();
            proxy.setHttpProxy(ipAndPort);
            proxy.setProxyType(Proxy.ProxyType.MANUAL);
            proxy.setAutodetect(false);
            DesiredCapabilities dcaps = getDesiredCapabilitiesProxy(proxy);

            //创建无界面浏览器对象
            driver = new PhantomJSDriver(dcaps);

            Integer sleepTime = second * 1000;
            //设置隐性等待（作用于全局）
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            //打开页面
            driver.get(url);

            Thread.sleep(sleepTime);

            //查找元素
            html = getHtmlString(html, driver);
            driver.close();
            // 关闭 ChromeDriver 接口
            driver.quit();
        } catch (Exception e) {

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return html;
    }


    public static String fetchHTMLContentByPhantomJs(String url, Integer second) {
        String html = new String();
        PhantomJSDriver driver = null;

        try {
            //设置必要参数
            DesiredCapabilities dcaps = getDesiredCapabilities();
//            String[] phantomJsArgs = {"--ignore-ssl-errors=true","--web-security=false","--ssl-protocol=any"};
//            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
//                    phantomJsArgs);
            //创建无界面浏览器对象
            driver = new PhantomJSDriver(dcaps);


            Integer sleepTime = second * 1000;
            //设置隐性等待（作用于全局）
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            //打开页面
            driver.get(url);

            Thread.sleep(sleepTime);

            //查找元素
            html = getHtmlString(html, driver);
            driver.close();
            // 关闭 ChromeDriver 接口
            driver.quit();
        } catch (Exception e) {

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return html;
    }


    public static List<CrawlerVarietyURL> fetchHTMLContentByPhantomJs(String url, Integer second, String a1) {
        String html = new String();
        PhantomJSDriver webDriver = null;
        List<CrawlerVarietyURL> soapURLList = new ArrayList<>();
        try {
            DesiredCapabilities dcaps = getDesiredCapabilities();

            //创建无界面浏览器对象
            webDriver = new PhantomJSDriver(dcaps);

            Integer sleepTime = second * 1000;
            //设置隐性等待（作用于全局）
            webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            //打开页面
            webDriver.get(url);
            Thread.sleep(sleepTime);


            webDriver.close();
            // 关闭 ChromeDriver 接口
        } catch (Exception e) {

        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        return soapURLList;
    }


    private static DesiredCapabilities getDesiredCapabilities() {
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
        return dcaps;
    }


    private static DesiredCapabilities getDesiredCapabilitiesProxy(Proxy proxy) {
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
        dcaps.setCapability(CapabilityType.PROXY, proxy);
        return dcaps;
    }

    private static void getVarietyList(List<CrawlerVarietyURL> soapURLList, Document document, String sorted, String varietyName) {
        Element contentElement = document.getElementById("albumpic-showall-wrap");
        Elements li = contentElement.select("li");
        for (Element e : li) {
            Element element = e.select("div").get(0).select("a").get(0);
            CrawlerVarietyURL crawlerVarietyURL = new CrawlerVarietyURL();


            crawlerVarietyURL.setTitle(element.select("img").attr("alt"));
            crawlerVarietyURL.setUrl(element.attr("href"));
            crawlerVarietyURL.setAddTime(DateUtils.getTodayDate());
            crawlerVarietyURL.setCategory("variety");
            crawlerVarietyURL.setPlatform("iqiyi");
            crawlerVarietyURL.setSorted(sorted);
            crawlerVarietyURL.setVarietyName(varietyName);

            soapURLList.add(crawlerVarietyURL);
        }
    }

    public static String fetchHTMLContentAndIframeByPhantomJs(String url, Integer second) throws Exception {
        //设置必要参数
        DesiredCapabilities dcaps = getDesiredCapabilities();
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

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,1600)");
        Thread.sleep(sleepTime);
        WebDriver commentIframe = driver.switchTo().frame("commentIframe");
        WebElement element = commentIframe.findElement(By.xpath("/html"));
        String outerHTML = element.getAttribute("outerHTML");

        driver.close();
        // 关闭 ChromeDriver 接口
        driver.quit();
        return html + "hxqh" + outerHTML;
    }


    public static List<CrawlerVarietyURL> fetchVarietyURLByPhantomJs(String url, Integer second, String sorted) {
        List<CrawlerVarietyURL> soapURLList = new ArrayList<>();
        String html = new String();
        PhantomJSDriver webDriver = null;

        try {
            //设置必要参数
            DesiredCapabilities dcaps = getDesiredCapabilities();
            //创建无界面浏览器对象
            webDriver = new PhantomJSDriver(dcaps);

            Integer sleepTime = second * 1000;
            //设置隐性等待（作用于全局）
            webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            //打开页面
            webDriver.get(url);
            Thread.sleep(sleepTime);


            html = getHtmlString(html, webDriver);
            // 先获取内容
            Document document = Jsoup.parse(html);
            Elements varietyEle = document.getElementsByClass("info-intro-title");
            String varietyName = varietyEle.get(0).text();

            getVarietyList(soapURLList, document, sorted, varietyName);
            int i = 0;

            for (; ; ) {
                WebElement a11 = null;
                WebElement div = webDriver.findElement(By.id("album_pic_paging"));
                if (i == 0) {
                    a11 = div.findElement(By.className("a1"));
                } else {
                    List<WebElement> x = div.findElements(By.className("a1"));
                    if (x.size() == 2) {
                        a11 = x.get(1);
                    } else {
                        break;
                    }
                }
                i++;
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", a11);
                html = getHtmlString(html, webDriver);
                document = Jsoup.parse(html);
                getVarietyList(soapURLList, document, sorted, varietyName);
            }


            webDriver.close();
            // 关闭 ChromeDriver 接口
            webDriver.quit();
        } catch (Exception e) {

        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        return soapURLList;
    }


    public static String fetchHTMLContent(String url, Integer second) throws Exception {
        List<CrawlerVarietyURL> soapURLList = new ArrayList<>();
        String html = new String();
        WebDriver webDriver = null;

        try {
            Integer sleepTime = second * 1000;
            System.getProperties().setProperty("webdriver.chrome.driver", Constants.CHROMEDRIVER);
            ChromeDriverService service = new ChromeDriverService
                    .Builder().usingDriverExecutable(new File(Constants.CHROMEDRIVER)).usingAnyFreePort().build();
            service.start();
            webDriver = new ChromeDriver();
            webDriver.get(url);
            Thread.sleep(sleepTime);
            WebElement webElement = webDriver.findElement(By.xpath("/html"));
            html = new String();
            if (webElement != null) {
                html = webElement.getAttribute("outerHTML");
            }
            webDriver.close();
            // 关闭 ChromeDriver 接口
            webDriver.quit();
        } catch (Exception e) {

        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
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


    private static String getHtmlString(String html, WebDriver webDriver) {
        //查找元素
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        if (webDriver != null) {
            html = webElement.getAttribute("outerHTML");
        }
        return html;
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
