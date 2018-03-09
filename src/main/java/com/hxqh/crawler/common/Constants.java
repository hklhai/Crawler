package com.hxqh.crawler.common;

/**
 * Created by Ocean lin on 2018/1/16.
 *
 * @author Lin
 */
public interface Constants {
    String os = System.getProperty("os.name");


    String CHROMEDRIVER = os.toLowerCase().startsWith("win") == true ? "E:\\Program\\ hromedriver.exe" : "/usr/bin/chromedriver";
    String SAVE_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler" : "/home/hadoop/crawler";
    String FILE_SPLIT = os.toLowerCase().startsWith("win") == true ? "\\" : "/";
    String FILE_LOC = os.toLowerCase().startsWith("win") == true ? "\\videos" : "/videos";
    String BOOK_JD_FILE_LOC = os.toLowerCase().startsWith("win") == true ? "\\book" : "/book";
    String HOST_SPARK1 = "spark1";
    String HOST_SPARK2 = "spark2";
    String HOST_SPARK3 = "spark3";
    String HOST_SPARK4 = "spark4";
    String MAOYAN_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler\\maoyan" : "/home/hadoop/crawler/maoyan";
    String MAOYAN_THREE_SECOND_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler\\maoyan_three" : "/home/hadoop/crawler/maoyan_three";
    String PHANTOMJS_PATH = os.toLowerCase().startsWith("win") == true ?
            "E:\\Program\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe" : "/usr/bin/phantomjs";


    /**
     * ElasticSearch 6.x config
     */
    Integer ES_PORT = 9300;


    /**
     * Linux 爬虫线程数据与各分区的数量
     */
    Integer IQIYI_THREAD_NUM = 10;
    Integer TENCENT_THREAD_NUM = 10;
    Integer JD_THREAD_NUM = 30;
    Integer DOUBAN_THREAD_NUM = 40;


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


    String MAO_YAN = "maoyan";

    String HIVE_SPLIT_LABEL = "\u0001";

    Integer BAIDU_LIMIT_NUM = 6;

    String BAIDU_SEARCH = "http://www.baidu.com/s?wd=";
}
