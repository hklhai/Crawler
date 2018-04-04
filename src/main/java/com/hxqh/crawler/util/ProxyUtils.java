package com.hxqh.crawler.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Random;

/**
 * Created by Ocean lin on 2018/4/4.
 *
 * @author Ocean lin
 */

@Configuration
public class ProxyUtils {

    @Autowired
    private Environment env;

    @Bean
    public String getProxyIpAndPort() {
        String ips = env.getProperty("crawler.ip.port");
        String[] splits = ips.split(",");

        Random random = new Random();
        int i = random.nextInt(splits.length);

        return splits[i];
    }
}