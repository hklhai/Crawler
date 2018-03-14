package com.hxqh.crawler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ocean lin on 2018/3/14.
 *
 * @author Ocean lin
 */

@Entity
@Table(name = "crawler_url_literature")
public class CrawlerLiteratureURL {
    @Id
    @GeneratedValue
    private Integer lid;

    private String title;
    private String url;
    private String addTime;
    private String platform;
    private String sorted;

    public CrawlerLiteratureURL() {
    }

    public CrawlerLiteratureURL(String title, String url, String addTime, String platform, String sorted) {
        this.title = title;
        this.url = url;
        this.addTime = addTime;
        this.platform = platform;
        this.sorted = sorted;
    }

    public Integer getLid() {
        return lid;
    }

    public void setLid(Integer lid) {
        this.lid = lid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }
}
