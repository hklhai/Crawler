package com.hxqh.crawler.controller;

import com.hxqh.crawler.common.Constants;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.rmi.UnknownHostException;

/**
 * Created by Ocean lin on 2017/10/10.
 *
 * @author Ocean lin
 */
@Configuration
public class ESConfig {
    @Bean
    public TransportClient client() throws UnknownHostException {

        /**
         * v5.x
         */
//        InetSocketTransportAddress node1 = new InetSocketTransportAddress(
//                InetAddress.getByName("spark3"), 9300
//        );
//        //可以new 三个节点，将node1放入,还可以放入node2...
//
//        Settings settings = Settings.builder().put("cluster.name", "elasticsearch")
//                .put("client.transport.sniff",true).build();
//
//        TransportClient client = new PreBuiltTransportClient(settings);
//        client.addTransportAddress(node1);
//        return client;

        /**
         * v6.x
         */
        TransportClient client = null;
        try {
            /**
             * put("client.transport.sniff", true)
             * 设置客户端嗅探整个集群状态，把集群中其他机器的IP地址加到客户端中
             * 不需要手动设置集群中所有的IP到连接客户端，自动添加，并自动发现新加入集群的机器。
             */
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", "es-market-analysis").build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(Constants.HOST_SPARK3), Constants.ES_PORT));
        } catch (Exception ex) {
            client.close();
        } finally {

        }
        return client;
    }

}
