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
    private String label;
    private String addtime;

    public CrawlerProblem() {
    }

    public CrawlerProblem(String url, String label, String addtime) {
        this.url = url;
        this.label = label;
        this.addtime = addtime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
