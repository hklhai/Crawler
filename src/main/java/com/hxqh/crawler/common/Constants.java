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
    String SAVE_LITERATURE_PATH = os.toLowerCase().startsWith("win") == true ? "E:\\crawler\\literature" : "/home/hadoop/crawler/literature";

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
    Integer IQIYI_VARIETY_THREAD_NUM = 35;
    Integer TENCENT_THREAD_NUM = 10;
    Integer JD_THREAD_NUM = 10;
    Integer DOUBAN_THREAD_NUM = 10;


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
    Integer DEFAULT_SEELP_SECOND_IQIYI = 30;
    Integer MORE_SEELP_SECOND_IQIYI = 50;
    Integer DEFAULT_SEELP_SECOND_JD_BOOK = 6;

    Integer TEN_THOUSAND = 10000;
    Integer BILLION = 100000000;


    String MAO_YAN = "maoyan";


    Integer BAIDU_LIMIT_NUM = 6;
    String BAIDU_SEARCH = "http://www.baidu.com/s?wd=";


    Integer IQIYI_VARIETY_WAIT_TIME = 4;
    String IQIYI_VARIETY_COLON = "：";

    /**
     * 图书
     */
    String BOOK_INDEX = "market_book2";
    String BOOK_TYPE = "book";

    /**
     * 猫眼
     */
    String MAOYAN_INDEX = "maoyan";
    String MAOYAN_TYPE = "film";

    /**
     * 电视剧 & 电影 & 综艺
     */
    String FILM_SOAP_VARIETY_INDEX = "film_data";
    String FILM_SOAP_VARIETY_TYPE = "film";


    /**
     * 网络文学 URL
     */
    Integer THREAD_NUM_17K = 3;
    Integer DEFAULT_SEELP_SECOND_17K = 12;

    String LITERATURE_INDEX = "market_literature";
    String LITERATURE_TYPE = "literature";

    String LITERATURE_URL_INDEX = "history_url_literature";
    String LITERATURE_URL_TYPE = "literature";


    /**
     * 电视剧 & 电影 URL
     */
    String SOAP_URL_INDEX = "history_url_film_soap";
    String SOAP_URL_TYPE = "film_soap";


    /**
     * 综艺 URL
     */
    String HISTORY_VARIETY_URL_INDEX = "history_url_variety";
    String HISTORY_VARIETY_URL_TYPE = "variety";
    String HISTORY_VARIETY_INDEX = "history_variety";
    String HISTORY_VARIETY_TYPE = "variety";


    /**
     * 图书  URL
     */
    String BOOK_URL_INDEX = "history_url_book";
    String BOOK_URL_TYPE = "book";

    /**
     * 豆瓣
     */
    Integer DOUBAN_NUM = 4;
    String DOUBAN_SEARCH_URL = "https://www.douban.com/search?q=";


    Integer PAGE = 0;
    Integer SIZE = 10000;
}
