package com.hxqh.crawler.test;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Ocean lin on 2018/1/25.
 *
 * @author Lin
 */
public class StartCrawlerTest {

//    @Test
//    public void test() {
//        InetAddress addr = null;
//        try {
//            addr = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        String hostName = addr.getHostName().toString(); //获取本机计算机名称
//        System.out.println("本机名称:" + hostName);
//    }
//
//
//    @Test
//    public void os() {
//        String os = System.getProperty("os.name");
//        if (os.toLowerCase().startsWith("win")) {
//            System.out.println("win");
//        } else if (os.toLowerCase().startsWith("linux")) {
//            System.out.println("Linux");
//        }
//    }


    @Test
    public void testSOH() {
        String s = "0\u0001user0\u0001name0\u000119\u0001professional44\u0001city96\u0001male\n";
        System.out.println(s);
    }


}