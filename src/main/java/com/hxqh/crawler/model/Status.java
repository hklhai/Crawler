package com.hxqh.crawler.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Ocean lin on 2018/9/13.
 *
 * @author Ocean lin
 */
@Entity
@Table(name = "tb_status")
public class Status {

    @Id
    @GeneratedValue
    private Integer sid;
    private Integer book;
    private Integer iqiyi;
    private Integer literature;
    private Integer maoyan;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date adddate;

    public Status() {
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getBook() {
        return book;
    }

    public void setBook(Integer book) {
        this.book = book;
    }

    public Integer getIqiyi() {
        return iqiyi;
    }

    public void setIqiyi(Integer iqiyi) {
        this.iqiyi = iqiyi;
    }

    public Integer getLiterature() {
        return literature;
    }

    public void setLiterature(Integer literature) {
        this.literature = literature;
    }

    public Integer getMaoyan() {
        return maoyan;
    }

    public void setMaoyan(Integer maoyan) {
        this.maoyan = maoyan;
    }

    public Date getAdddate() {
        return adddate;
    }

    public void setAdddate(Date adddate) {
        this.adddate = adddate;
    }
}
