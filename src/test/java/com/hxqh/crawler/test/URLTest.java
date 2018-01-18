package com.hxqh.crawler.test;

import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class URLTest {

    private static final Integer PAGE_NUM = 30;
    private static final String URL = "<a data-searchpingback-elem=\"link\" data-searchpingback-param=\"target=3964b0adbecf7fe307271420f47339a6&amp;ptype=1&amp;site=iqiyi&amp;pos=1\" rseat=\"bigTitle\" title=\"前任2：备胎反击战\" href=\"http://www.iqiyi.com/v_19rrkih1u4.html#vfrm=2-4-0-1\" target=\"_blank\">前任2：备胎反击战</a>\n";

    @Resource
    private CrawlerURLRepository crawlerURLRepository;

    /**
     * 生成待爬取页面集合
     * <p>
     * http://list.iqiyi.com/www/1/----------0---11-1-1-iqiyi--.html
     * http://list.iqiyi.com/www/1/----------0---11-2-1-iqiyi--.html
     * <p>
     * <p>
     * http://list.iqiyi.com/www/1/----------0---11-30-1-iqiyi--.html
     */
    @Test
    public void testAppAndPersist() {
        // 所有待爬取URLList
        List<String> allStartURLList = new ArrayList<>();

        ArrayList<String> allHrefList = new ArrayList();
        ArrayList<String> hrefList = new ArrayList();

        // 1.获取全部页面Url
        String prefix = "http://list.iqiyi.com/www/1/----------0---11-1-";
        String suffix = "-iqiyi--.html";

        for (int i = 1; i <= PAGE_NUM; i++) {
            String url = prefix + i + suffix;
            allStartURLList.add(url);
        }

        for (String s : allStartURLList) {
            try {
                String outerHTML = CrawlerUtils.fetchHTMLContent(s);

                String[] split = outerHTML.split("\n");
                for (int i = 0; i < split.length; i++) {
                    String href = getHref(split[i]);
                    if (href != null)
                        allHrefList.add(href);
                }

                for (int i = 0; i < allHrefList.size(); i++) {
                    if (allHrefList.get(i).contains("vfrm=2-4-0-1")) {
                        hrefList.add(allHrefList.get(i));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 3.解析


        List<CrawlerURL> crawlerURLS = new ArrayList<>();
        // . 取每个页面的需要持久化URL
        for (String s : hrefList) {
            Document doc = Jsoup.parse(s);
            String title = doc.select("a").get(0).attr("title").toString();
            String url = doc.select("a").get(0).attr("href").toString();
            String addTime = DateUtils.getTodayDate();
            CrawlerURL crawlerURL = new CrawlerURL(title, url, addTime);
            crawlerURLS.add(crawlerURL);
        }
        crawlerURLRepository.save(crawlerURLS);
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
