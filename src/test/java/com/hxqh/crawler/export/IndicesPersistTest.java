package com.hxqh.crawler.export;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.repository.StatusRepository;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;

/**
 * Created by Ocean lin on 2018/09/13.
 *
 * @author Lin
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class IndicesPersistTest {

    TransportClient client = null;

    @Autowired
    private StatusRepository statusRepository;


    @Before
    public void init() throws Exception {
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
    }

    //    @Test
    public void test() {
    }
}
