package com.hxqh.crawler.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.CompanyDto;
import com.hxqh.crawler.model.CrawlerURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ocean lin on 2018/3/7.
 *
 * @author Ocean lin
 */
public class BaiduCompanyUtils {

    /**
     * @return encode后的title
     */
    public static String encodeString(CrawlerURL crawlerURL) {
        try {
            String encodeUrl = URLEncoder.encode(crawlerURL.getTitle(), "UTF-8");
            return encodeUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static CompanyDto getCompany(String s) {
        Map<String, String> detailMap = new HashMap<>();
        try {
            String html = CrawlerUtils.fetchHTMLContentByPhantomJs(Constants.BAIDU_SEARCH + s, 2);
            Document doc = Jsoup.parse(html);
            for (int j = 1; j <= Constants.BAIDU_LIMIT_NUM; j++) {
                Element element = doc.getElementById(String.valueOf(j));
                Element element1 = element.select("h3").get(0);
                Element a = element1.select("a").get(0);
                String title = a.text();
                if (title.contains("_百度百科")) {
                    String href = element.select("h3")
                            .get(0).select("a").get(0).attr("href");

                    String key = title;
                    detailMap.put(key, href);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CompanyDto companyDto = new CompanyDto();

        // 获取电影公司名称
        for (Map.Entry<String, String> entry : detailMap.entrySet()) {
            String url = entry.getValue();
            try {
                Document doc = Jsoup.parse(new URL(url),2000);
                Elements keys = doc.getElementsByClass("basicInfo-item name");
                Elements values = doc.getElementsByClass("basicInfo-item value");

                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < keys.size(); i++) {
                    Element keyEle = keys.get(i);
                    Element valuesEle = values.get(i);
                    map.put(keyEle.text(), valuesEle.text());
                }

                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(map));
                String result = jsonObject.toString();

                companyDto.setIssueCompany(map.get("发行公司"));
                companyDto.setProductCompany(map.get("出品公司"));
                companyDto.setOtherInfo(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return companyDto;
    }


}
