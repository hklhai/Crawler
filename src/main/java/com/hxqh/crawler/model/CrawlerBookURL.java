package com.hxqh.crawler.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ocean lin on 2018/1/18.
 */
@Entity
@Table(name = "crawler_url_book")
public class CrawlerBookURL {

    @Id
    private String url;
    private String title;
    private String addTime;
    private String category;
    private String platform;

    public CrawlerBookURL() {
    }

    public CrawlerBookURL(String url, String title, String addTime, String category, String platform) {
        this.url = url;
        this.title = title;
        this.addTime = addTime;
        this.category = category;
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
