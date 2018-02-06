package com.hxqh.crawler.common;

/**
 * Created by Ocean lin on 2018/1/16.
 */
public interface Constants {

    /**
     * Windows
     */
    String CHROMEDRIVER = "E://Program//chromedriver.exe";
    String SAVE_PATH = "E://crawler";
    String FILE_SPLIT = "\\";
    String FILE_LOC = "\\videos";
    String BOOK_JD_FILE_LOC = "\\book";
    String HOST_SPARK1 = "spark1";
    String HOST_SPARK2 = "spark2";
    String HOST_SPARK3 = "spark3";
    String HOST_SPARK4 = "spark4";
    /**
     * ElasticSearch 6.x config
     */
    String ES_HOST = "127.0.0.1";
    Integer ES_PORT = 9300;
    String MAOYAN_PATH = "E:\\crawler\\maoyan";


    /**
     * Linux
     */
//    String CHROMEDRIVER = "/usr/bin/chromedriver";
//    String SAVE_PATH = "/home/hadoop/crawler";
//    String FILE_SPLIT = "/";
//    String FILE_LOC = "/videos";
//    String BOOK_JD_FILE_LOC = "/book";
//    String HOST_SPARK1 = "spark1";
//    String HOST_SPARK2 = "spark2";
//    String HOST_SPARK3 = "spark3";
//    String HOST_SPARK4 = "spark4";
//    /**
//     * ElasticSearch 6.x config
//     */
//    Integer ES_PORT = 9300;


    /**
     * Linux 爬虫线程数据与各分区的数量
     */
    Integer IQIYI_THREAD_NUM = 4;
    Integer IQIYI_PARTITION_NUM = 600; // 2319部电影

    Integer TENCENT_THREAD_NUM = 3;
    Integer TENCENT_PARTITION_NUM = 10; // 900部电影

    Integer JD_THREAD_NUM = 5;
    Integer JD_PARTITION_NUM = 990;  // 4916本书


    // 起始页数
    Integer PAGE_START_NUM = 1;
    // 结束页数
    Integer PAGE_END_NUM = 30;

    // 腾讯视频起始页数
    Integer TENCENT_PAGE_START_NUM = 0;
    // 结束页数
    Integer TENCENT_PAGE_END_NUM = 30;

    /**
     *
     */
    String HDFS_URL = "hdfs://spark1:9000";
    Integer DEFAULT_SEELP_SECOND = 15;
    Integer DEFAULT_SEELP_SECOND_JD_BOOK = 6;

    Integer TEN_THOUSAND = 10000;
    Integer BILLION = 100000000;

    // 猫眼数据下载周期1小时一次
    Integer ONE_HOUR = 3600000;


}
