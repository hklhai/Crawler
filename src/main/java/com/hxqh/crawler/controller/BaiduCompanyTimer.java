package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.CompanyDto;
import com.hxqh.crawler.model.BaiduInfo;
import com.hxqh.crawler.model.VBaiduCrawler;
import com.hxqh.crawler.repository.VBaiduCrawlerRepository;
import com.hxqh.crawler.repository.VDouBanCrawlerFilmRepository;
import com.hxqh.crawler.service.CrawlerService;
import com.hxqh.crawler.util.BaiduCompanyUtils;
import com.hxqh.crawler.util.HostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Ocean lin on 2018/4/4.
 *
 * @author Ocean lin
 */
@Component
public class BaiduCompanyTimer {

    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private VBaiduCrawlerRepository vBaiduCrawlerRepository;

    @Scheduled(cron = "0 10 17 * * ?")
    public void baiduFilmCompany() {
        try {
            if (HostUtils.getHostName().equals(Constants.HOST_SPARK2)) {

                List<VBaiduCrawler> crawlerList = vBaiduCrawlerRepository.findAll();

                List<VBaiduCrawler> urlList = crawlerList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                        -> new TreeSet<>(Comparator.comparing(o -> o.getUrl()))), ArrayList::new));
                // todo IP池配置后可以开放设置
                // 每天爬取800条
                urlList = urlList.subList(0, 800);

                // 调用百度API，查询后获取company信息存储至，将附属信息同样保留
                for (int i = 0; i < urlList.size(); i++) {
                    VBaiduCrawler vBaiduCrawler = urlList.get(i);
                    String s = BaiduCompanyUtils.encodeString(vBaiduCrawler);
                    CompanyDto companyDto = BaiduCompanyUtils.getCompany(s);

                    BaiduInfo baiduInfo = new BaiduInfo();
                    baiduInfo.setCompany(companyDto.getProductCompany());
                    baiduInfo.setIssue(companyDto.getIssueCompany());
                    baiduInfo.setSource(vBaiduCrawler.getPlatform());
                    baiduInfo.setName(vBaiduCrawler.getTitle());
                    baiduInfo.setOtherInfo(companyDto.getOtherInfo());
                    crawlerService.save(baiduInfo);
                    System.out.println("persist " + vBaiduCrawler.getTitle() + " Success!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
