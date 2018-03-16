package com.hxqh.crawler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ocean lin on 2018/3/12.
 *
 * @author Ocean lin
 */
@Entity
@Table(name = "crawler_variety")
public class CrawlerVariety {

    @Id
    @GeneratedValue
    private Integer vid;

    private String url;
    private String addTime;
    private String sorted;

    public CrawlerVariety() {
    }

    public CrawlerVariety(String url, String addTime, String sorted) {
        this.url = url;
        this.addTime = addTime;
        this.sorted = sorted;
    }

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
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

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }
}
