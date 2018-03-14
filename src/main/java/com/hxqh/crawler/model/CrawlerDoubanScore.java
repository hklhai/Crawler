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
    private Float scorevalue;
    private Integer scorenum;

    public CrawlerDoubanScore() {
    }

    public CrawlerDoubanScore(String category, String title, Float scorevalue, Integer scorenum) {
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

    public Float getScorevalue() {
        return scorevalue;
    }

    public void setScorevalue(Float scorevalue) {
        this.scorevalue = scorevalue;
    }

    public Integer getScorenum() {
        return scorenum;
    }

    public void setScorenum(Integer scorenum) {
        this.scorenum = scorenum;
    }
}
