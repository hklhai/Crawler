package com.hxqh.crawler.domain;

/**
 * Created by Ocean lin on 2018/1/18.
 */
public class VideosFilm {

    private String source;
    private String filmName;
    private String star;
    private String director;
    private String category;
    private String label;
    private float scoreVal;
    private long commentNum;
    private long up;
    private String addTime;
    private long playNum;

//     private String url;
//    private String platform;
//    private String sorted;

    public VideosFilm() {
    }

    public VideosFilm(String source, String filmName, String star, String director, String category, String label, float scoreVal, long commentNum, long up, String addTime, long playNum) {
        this.source = source;
        this.filmName = filmName;
        this.star = star;
        this.director = director;
        this.category = category;
        this.label = label;
        this.scoreVal = scoreVal;
        this.commentNum = commentNum;
        this.up = up;
        this.addTime = addTime;
        this.playNum = playNum;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getScoreVal() {
        return scoreVal;
    }

    public void setScoreVal(float scoreVal) {
        this.scoreVal = scoreVal;
    }

    public long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(long commentNum) {
        this.commentNum = commentNum;
    }

    public long getUp() {
        return up;
    }

    public void setUp(long up) {
        this.up = up;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public long getPlayNum() {
        return playNum;
    }

    public void setPlayNum(long playNum) {
        this.playNum = playNum;
    }
}
