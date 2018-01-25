package com.hxqh.crawler.common;

/**
 * Created by Ocean lin on 2018/1/16.
 */
public interface Constants {

    /**
     * Windows
     */
//    String CHROMEDRIVER = "E://Program//chromedriver.exe";
//    String SAVE_PATH = "E://crawler";
//    String FILE_SPLIT = "\\";
//    String FILE_LOC = "\\videos";
    /**
     * 爬虫线程数据与各分区的数量
     */
//    Integer THREAD_NUM = 3;
//    Integer PARTITION_NUM = 4;





    /**
     * Linux
     */
    String CHROMEDRIVER = "/usr/bin/chromedriver";
    String SAVE_PATH = "/home/hadoop/crawler";
    String FILE_SPLIT = "/";
    String FILE_LOC = "/videos";

    /**
     * Linux 爬虫线程数据与各分区的数量
     */
    Integer THREAD_NUM = 4;
    Integer PARTITION_NUM = 500;

    // 起始页数
    Integer PAGE_START_NUM = 1;
    // 结束页数
    Integer PAGE_END_NUM = 30;

    /**
     *
     */
    String HDFS_URL = "hdfs://spark1:9000";


}
