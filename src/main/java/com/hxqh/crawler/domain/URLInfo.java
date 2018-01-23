package com.hxqh.crawler.domain;

/**
 * Created by Ocean lin on 2018/1/23.
 */
public class URLInfo {

    private String platform;

    private String category;

    private String sorted;


    public URLInfo() {
    }

    public URLInfo(String platform, String category, String sorted) {
        this.platform = platform;
        this.category = category;
        this.sorted = sorted;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }
}
