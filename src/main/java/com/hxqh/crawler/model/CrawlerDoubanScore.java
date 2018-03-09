package com.hxqh.crawler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "crawler_douban_score")
public class CrawlerDoubanScore {

    @Id
    @GeneratedValue
    private Integer did;
    private String category;
    private String title;
    private String scorevalue;
    private String scorenum;

    public CrawlerDoubanScore() {
    }

    public String getScorevalue() {
        return scorevalue;
    }

    public void setScorevalue(String scorevalue) {
        this.scorevalue = scorevalue;
    }

    public CrawlerDoubanScore(String category, String title, String scorevalue, String scorenum) {
        this.category = category;
        this.title = title;
        this.scorevalue = scorevalue;
        this.scorenum = scorenum;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getScorenum() {
        return scorenum;
    }

    public void setScorenum(String scorenum) {
        this.scorenum = scorenum;
    }


}
