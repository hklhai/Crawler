package com.hxqh.crawler.domain;

import java.util.Date;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Ocean lin
 */
public class RealTimeMovie {


    // 来源、电影名称、实时综合票房（万）、累计票房、实时分账票房、累计分账票房、排片场次、新增时间
    private String platform;
    private String filmName;

    private Double boxInfo;
    private Double sumBoxInfo;

    private Double splitBoxInfo;
    private Double splitSumBoxInfo;

    private String releaseInfo;
    private Date addTime;

    private Integer showInfo;

    public RealTimeMovie() {
    }

    public RealTimeMovie(String platform, String filmName, Double boxInfo, Double sumBoxInfo, Double splitBoxInfo, Double splitSumBoxInfo, String releaseInfo, Date addTime, Integer showInfo) {
        this.platform = platform;
        this.filmName = filmName;
        this.boxInfo = boxInfo;
        this.sumBoxInfo = sumBoxInfo;
        this.splitBoxInfo = splitBoxInfo;
        this.splitSumBoxInfo = splitSumBoxInfo;
        this.releaseInfo = releaseInfo;
        this.addTime = addTime;
        this.showInfo = showInfo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public Double getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(Double boxInfo) {
        this.boxInfo = boxInfo;
    }

    public Double getSumBoxInfo() {
        return sumBoxInfo;
    }

    public void setSumBoxInfo(Double sumBoxInfo) {
        this.sumBoxInfo = sumBoxInfo;
    }

    public Double getSplitBoxInfo() {
        return splitBoxInfo;
    }

    public void setSplitBoxInfo(Double splitBoxInfo) {
        this.splitBoxInfo = splitBoxInfo;
    }

    public Double getSplitSumBoxInfo() {
        return splitSumBoxInfo;
    }

    public void setSplitSumBoxInfo(Double splitSumBoxInfo) {
        this.splitSumBoxInfo = splitSumBoxInfo;
    }


    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }


    public String getReleaseInfo() {
        return releaseInfo;
    }

    public void setReleaseInfo(String releaseInfo) {
        this.releaseInfo = releaseInfo;
    }

    public Integer getShowInfo() {
        return showInfo;
    }

    public void setShowInfo(Integer showInfo) {
        this.showInfo = showInfo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RealTimeMovie{");
        sb.append("platform='").append(platform).append('\'');
        sb.append(", filmName='").append(filmName).append('\'');
        sb.append(", boxInfo=").append(boxInfo);
        sb.append(", sumBoxInfo=").append(sumBoxInfo);
        sb.append(", splitBoxInfo=").append(splitBoxInfo);
        sb.append(", splitSumBoxInfo=").append(splitSumBoxInfo);
        sb.append(", releaseInfo='").append(releaseInfo).append('\'');
        sb.append(", addTime=").append(addTime);
        sb.append(", showInfo=").append(showInfo);
        sb.append('}');
        return sb.toString();
    }
}
