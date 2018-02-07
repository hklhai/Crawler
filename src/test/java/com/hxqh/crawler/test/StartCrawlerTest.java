package com.hxqh.crawler.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Ocean lin on 2018/1/25.
 * @author Lin
 */
public class StartCrawlerTest {

    //    @Test
    public void test() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostName = addr.getHostName().toString(); //获取本机计算机名称
        System.out.println("本机名称:" + hostName);
    }

}