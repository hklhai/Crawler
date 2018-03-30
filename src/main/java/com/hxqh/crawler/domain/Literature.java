package com.hxqh.crawler.domain;

/**
 * Created by Ocean lin on 2018/3/20.
 *
 * @author Ocean lin
 */
public class Literature {

    private String platform;
    private String name;
    private String author;
    private String mainclass;
    private String subclass;
    private String label;
    private Long fans;
    private Integer commentnum;
    private Long clicknum;

    public Literature(String platform, String name, String author, String mainclass, String subclass, String label, Long fans, Integer commentnum, Long clicknum) {
        this.platform = platform;
        this.name = name;
        this.author = author;
        this.mainclass = mainclass;
        this.subclass = subclass;
        this.label = label;
        this.fans = fans;
        this.commentnum = commentnum;
        this.clicknum = clicknum;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMainclass() {
        return mainclass;
    }

    public void setMainclass(String mainclass) {
        this.mainclass = mainclass;
    }

    public String getSubclass() {
        return subclass;
    }

    public void setSubclass(String subclass) {
        this.subclass = subclass;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public Integer getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(Integer commentnum) {
        this.commentnum = commentnum;
    }

    public Long getClicknum() {
        return clicknum;
    }

    public void setClicknum(Long clicknum) {
        this.clicknum = clicknum;
    }
}
