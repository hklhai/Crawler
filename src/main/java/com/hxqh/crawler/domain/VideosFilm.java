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
    private Integer commentNum;
    private Integer up;
    private String addTime;
    private Integer playNum;

//     private String url;
//    private String platform;
//    private String sorted;

    public VideosFilm() {
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

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Integer getUp() {
        return up;
    }

    public void setUp(Integer up) {
        this.up = up;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public Integer getPlayNum() {
        return playNum;
    }

    public void setPlayNum(Integer playNum) {
        this.playNum = playNum;
    }
}
