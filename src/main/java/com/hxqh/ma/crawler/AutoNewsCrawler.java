package com.hxqh.ma.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.hxqh.ma.util.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Ocean
 */
public class AutoNewsCrawler extends BreadthCrawler {
    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     * information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     * links which match regex rules from pag
     */

    JdbcTemplate jdbcTemplate = null;

    public AutoNewsCrawler(String crawlPath, boolean autoParse) {


        super(crawlPath, autoParse);
        /*start page*/
        this.addSeed("http://news.hfut.edu.cn/list-1-1.html");

        /*fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml*/
        this.addRegex("http://news.hfut.edu.cn/show-.*html");
        /*do not fetch jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        this.addRegex("-.*#.*");

        setThreads(50);
        setTopN(100);


        try {
            jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                    "jdbc:mysql://spark4/market_analysis?useUnicode=true&characterEncoding=utf8",
                    "root", "mysql", 5, 30);

            /*创建数据表*/
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_content ("
                    + "id int(11) NOT NULL AUTO_INCREMENT,"
                    + "title varchar(200),url varchar(200),html longtext,"
                    + "PRIMARY KEY (id)"
                    + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
            System.out.println("成功创建数据表 tb_content");
        } catch (Exception ex) {
            jdbcTemplate = null;
            System.out.println("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
        }

//        setResumable(true);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();
        /*if page is news page*/
        if (page.matchUrl("http://news.hfut.edu.cn/show-.*html")) {

            /*extract title and content of news by css selector*/
            String title = page.select("div[id=Article]>h2").first().text();
            String content = page.select("div#artibody").text();

            System.out.println("URL:\n" + url);
            System.out.println("title:\n" + title);
            System.out.println("content:\n" + content);

            /*If you want to add urls to crawl,add them to nextLink*/
            /*WebCollector automatically filters links that have been fetched before*/
            /*If autoParse is true and the link you add to nextLinks does not match the
              regex rules,the link will also been filtered.*/
            //next.add("http://xxxxxx.com");


            if (jdbcTemplate != null) {
                int updates = jdbcTemplate.update("insert into tb_content"
                                + " (title,url,html) value(?,?,?)",
                        title, page.getUrl(), page.getHtml());
                if (updates == 1) {
                    System.out.println("mysql插入成功");
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {
        AutoNewsCrawler crawler = new AutoNewsCrawler("crawl", true);
        /*start crawl with depth of 4*/
        crawler.start(4);
    }

}