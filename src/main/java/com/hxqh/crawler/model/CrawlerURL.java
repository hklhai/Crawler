package com.hxqh.crawler.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ocean lin on 2018/1/18.
 */
@Entity
@Table(name = "crawler_url")
public class CrawlerURL {


    private String title;
    @Id
    private String url;
    private String addTime;
    private String category;
    private String platform;


    public CrawlerURL() {
    }

    public CrawlerURL(String title, String url, String addTime) {
        this.title = title;
        this.url = url;
        this.addTime = addTime;
    }

    public CrawlerURL(String title, String url, String addTime, String category, String platform) {
        this.title = title;
        this.url = url;
        this.addTime = addTime;
        this.category = category;
        this.platform = platform;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CrawlerURL{");
        sb.append("title='").append(title).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", addTime='").append(addTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}