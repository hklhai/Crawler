package com.hxqh.crawler.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ocean lin on 2018/1/19.
 */
@Entity
@Table(name = "crawler_problem")
public class CrawlerProblem {

    @Id
    private String url;
    private String addtime;
    private Integer success;
    private String category;
    private String platform;
    private String sorted;


    public CrawlerProblem() {
    }

    public CrawlerProblem(String url, String addtime, Integer success, String category, String platform, String sorted) {
        this.url = url;
        this.addtime = addtime;
        this.success = success;
        this.category = category;
        this.platform = platform;
        this.sorted = sorted;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
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

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }
}
