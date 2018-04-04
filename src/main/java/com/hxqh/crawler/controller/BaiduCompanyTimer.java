package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.CompanyDto;
import com.hxqh.crawler.model.BaiduInfo;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.BaiduInfoRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.util.BaiduCompanyUtils;
import com.hxqh.crawler.util.HostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Ocean lin on 2018/4/4.
 *
 * @author Ocean lin
 */
@Component
public class BaiduCompanyTimer {

    @Autowired
    private BaiduInfoRepository baiduInfoRepository;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;


    @Scheduled(cron = "0 0 13 * * ?")
    public void baiduCompany() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK3)) {


                List<CrawlerURL> urlList = crawlerURLRepository.findAll();
                List<BaiduInfo> baiduInfoList = baiduInfoRepository.findAll();
                for (int i = 0; i < urlList.size(); i++) {
                    for (int j = 0; j < baiduInfoList.size(); j++) {
                        CrawlerURL crawlerURL = urlList.get(i);
                        BaiduInfo baiduInfo = baiduInfoList.get(j);
                        if (crawlerURL.getTitle().equals(baiduInfo.getName()) &&
                                crawlerURL.getPlatform().equals(baiduInfo.getSource())) {
                            urlList.remove(crawlerURL);
                        }
                    }
                }

                // todo IP池配置后可以开放设置
                // 防止每天爬取400条
                urlList = urlList.subList(0, 400);

                // 调用百度API，查询后获取company信息存储至，将附属信息同样保留
                for (int i = 0; i < urlList.size(); i++) {
                    CrawlerURL crawlerURL = urlList.get(i);
                    String s = BaiduCompanyUtils.encodeString(crawlerURL);
                    CompanyDto companyDto = BaiduCompanyUtils.getCompany(s);

                    BaiduInfo baiduInfo = new BaiduInfo();
                    baiduInfo.setCompany(companyDto.getProductCompany());
                    baiduInfo.setIssue(companyDto.getIssueCompany());
                    baiduInfo.setSource(crawlerURL.getPlatform());
                    baiduInfo.setName(crawlerURL.getTitle());
                    baiduInfo.setOtherInfo(companyDto.getOtherInfo());
                    crawlerService.save(baiduInfo);
                    System.out.println("persist " + crawlerURL.getTitle() + " Success!");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
