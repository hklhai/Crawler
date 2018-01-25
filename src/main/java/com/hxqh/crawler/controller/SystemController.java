package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.controller.thread.PersistFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.model.User;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.DateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Ocean lin on 2017/7/1.
 */
@Controller
@RequestMapping("/system")
public class SystemController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private CrawlerProblemRepository crawlerProblemRepository;

    /**
     * 页面跳转接口
     * http://127.0.0.1:8090/system/user
     *
     * @return
     */
    @RequestMapping("/user")
    public String user() {
        return "user/index";
    }

    /**
     * 数据接口
     * http://127.0.0.1:8090/system/userData?name=xdm
     *
     * @param name 用户名
     * @return
     */
    @RequestMapping("userData")
    @ResponseBody
    public User userData(@RequestParam(value = "name") String name) {
        return systemService.findUserById(name);
    }


    /**
     * 生成待爬取页面集合 电影
     * <p>
     * http://list.iqiyi.com/www/1/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/1/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/1/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/1/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * http://list.iqiyi.com/www/1/-------------8-1-1-iqiyi--.html  评分
     * http://list.iqiyi.com/www/1/-------------8-2-1-iqiyi--.html  评分
     * <p>
     * <p>
     * <p>
     * 生成待爬取页面集合 电视剧
     * http://list.iqiyi.com/www/2/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/2/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/2/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/2/-------------4-2-1-iqiyi--.html  更新时间
     * <p>
     * <p>
     * <p>
     * 生成待爬取页面集合 综艺
     * http://list.iqiyi.com/www/6/-------------11-1-1-iqiyi--.html  热门
     * http://list.iqiyi.com/www/6/-------------11-2-1-iqiyi--.html  热门
     * <p>
     * http://list.iqiyi.com/www/6/-------------4-1-1-iqiyi--.html  更新时间
     * http://list.iqiyi.com/www/6/-------------4-2-1-iqiyi--.html  更新时间
     */
    @RequestMapping("/iqiyiurl")
    public String iqiyiurl() {

        return "crawler/notice";
    }


    /**
     * 爬取电影、影视、综艺数据并上传至HDFS
     * http://127.0.0.1:8090/system/iqiyi
     *
     * @return
     */
    @RequestMapping("/iqiyi")
    public String iqiyiFilm() {
        // 1. 从数据库获取待爬取链接
        List<CrawlerURL> crawlerURLS = crawlerURLRepository.findFilm();
        crawlerURLS = crawlerURLS.subList(39, 50);
        List<List<CrawlerURL>> lists = ListUtils.partition(crawlerURLS, Constants.PARTITION_NUM);

        ExecutorService service = Executors.newFixedThreadPool(Constants.THREAD_NUM);

        for (List<CrawlerURL> l : lists) {
            service.execute(new PersistFilm(l, crawlerProblemRepository, systemService));
        }
        service.shutdown();

        while (!service.isTerminated()) {
        }


        // 2.上传至HSDF
//        try {
//            persistToHDFS("-iqiyi", Constants.FILE_LOC);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "crawler/notice";
    }


    public void persistToHDFS(String paltform, String loc) throws URISyntaxException, IOException {
        Configuration conf = new Configuration();
        URI uri = new URI(Constants.HDFS_URL);
        FileSystem fs = FileSystem.get(uri, conf);
        String path = Constants.SAVE_PATH + Constants.FILE_SPLIT + DateUtils.getTodayDate() + paltform;
        Path resP = new Path(path);
        String location = loc + Constants.FILE_SPLIT +
                DateUtils.getTodayYear() + Constants.FILE_SPLIT + DateUtils.getTodayMonth();
        Path destP = new Path(location);
        if (!fs.exists(destP)) {
            fs.mkdirs(destP);
        }
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        fs.copyFromLocalFile(resP, destP);
        System.out.println("upload file " + name + " to HDFS");
        fs.close();
    }

}


