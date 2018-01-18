package com.hxqh.crawler.controller;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Ocean lin on 2017/10/10.
 */
@Configuration
public class ESConfig {
    @Bean
    public TransportClient client() throws UnknownHostException {
        InetSocketTransportAddress node1 = new InetSocketTransportAddress(
                InetAddress.getByName("192.168.89.161"), 9300
        );
        //可以new 三个节点，将node1放入,还可以放入node2...

        Settings settings = Settings.builder().put("cluster.name", "es-hk")
                .put("client.transport.sniff",true).build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node1);
        return client;
    }

}
