package com.hxqh.crawler.common;

/**
 * Created by Ocean lin on 2018/1/16.
 */
public interface Constants {

    /**
     * Windows
     */
//    String CHROMEDRIVER = "E://Program//chromedriver.exe";
//    String SAVE_PATH = "E://";
//    String FILE_SPLIT = "\\";

    /**
     * Linux
     */
    String CHROMEDRIVER = "/usr/bin/chromedriver";
    String SAVE_PATH = "/home/hadoop/crawler";
    String FILE_SPLIT = "/";


    /**
     * 爬虫线程数据与各分区的数量
     */
    Integer THREAD_NUM = 4;
    Integer PARTITION_NUM = 12;


    /**
     *
     */
    String HDFS_URL = "hdfs://spark1:9000";

}
