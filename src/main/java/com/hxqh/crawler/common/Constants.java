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
//    /**
//     * ElasticSearch 6.x config
//     */
//    String ES_HOST = "127.0.0.1";
//    Integer ES_PORT = 9300;

    /**
     * Linux
     */
    String CHROMEDRIVER = "/usr/bin/chromedriver";
    String SAVE_PATH = "/home/hadoop/crawler";
    String FILE_SPLIT = "/";
    String FILE_LOC = "/videos";
    /**
     * ElasticSearch 6.x config
     */
    String ES_HOST = "spark3";
    Integer ES_PORT = 9300;


    /**
     * Linux 爬虫线程数据与各分区的数量
     */
    Integer IQIYI_THREAD_NUM = 4;
    Integer IQIYI_PARTITION_NUM = 500;
    Integer JD_THREAD_NUM = 4;
    Integer JD_PARTITION_NUM = 5;


    // 起始页数
    Integer PAGE_START_NUM = 1;
    // 结束页数
    Integer PAGE_END_NUM = 30;

    /**
     *
     */
    String HDFS_URL = "hdfs://spark1:9000";
    Integer DEFAULT_SEELP_SECOND = 15;
    Integer DEFAULT_SEELP_SECOND_JD_BOOK = 6;

    Integer TEN_THOUSAND = 10000;
    Integer BILLION = 100000000;


}
