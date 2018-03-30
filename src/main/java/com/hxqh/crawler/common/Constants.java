package com.hxqh.crawler.common;

/**
 * Created by Ocean lin on 2018/1/16.
 *
 * @author Lin
 */
public interface Constants {

    String os = System.getProperty("os.name");

    String CHROMEDRIVER = os.toLowerCase().startsWith("win") == true ? "E:\\Program\\chromedriver.exe" : "/usr/bin/chromedriver";
    String SAVE_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler" : "/home/hadoop/crawler";
    String SAVE_VARIETY_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler\\variety" : "/home/hadoop/crawler/variety";
    String SAVE_SOAP_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler\\soap" : "/home/hadoop/crawler/soap";
    String FILE_SPLIT = os.toLowerCase().startsWith("win") == true ? "\\" : "/";
    String FILE_LOC = os.toLowerCase().startsWith("win") == true ? "\\videos" : "/videos";
    String FILE_LOC_SOAP = os.toLowerCase().startsWith("win") == true ? "\\videos\\soap" : "/videos/soap";
    String FILE_LOC_VARIETY = os.toLowerCase().startsWith("win") == true ? "\\videos\\variety " : "/videos/variety";
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
    Integer IQIYI_VARIETY_THREAD_NUM = 20;

    Integer TENCENT_THREAD_NUM = 10;
    Integer JD_THREAD_NUM = 15;
    Integer DOUBAN_THREAD_NUM = 40;
    Integer QIDIAN_THREAD_NUM = 10;


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
    Integer DEFAULT_SEELP_SECOND_IQIYI = 15;
    Integer DEFAULT_SEELP_SECOND_JD_BOOK = 6;

    Integer TEN_THOUSAND = 10000;
    Integer BILLION = 100000000;


    String MAO_YAN = "maoyan";


    Integer BAIDU_LIMIT_NUM = 6;
    String BAIDU_SEARCH = "http://www.baidu.com/s?wd=";


    Integer IQIYI_VARIETY_WAIT_TIME = 4;
    String IQIYI_VARIETY_COLON = "：";

    String LITERATURE_INDEX = "market_literature";
    String LITERATURE_TYPE = "literature";

}
