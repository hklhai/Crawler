package com.hxqh.crawler.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Created by Lin on 18-2-1.
 *
 * @author Lin
 */
public class HostUtils {

    public static String getHostName() throws URISyntaxException, IOException {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostName = addr.getHostName().toString(); //获取本机计算机名称
        return hostName;
    }
}