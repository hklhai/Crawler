package com.hxqh.crawler.export;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.util.FileUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * Created by Ocean lin on 2018/7/3.
 *
 * @author Lin
 */

public class ElasticSearchExport {

    TransportClient client = null;

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

    /**
     * curl -H "Content-Type: application/x-ndjson" -XPOST "spark3:9200/film_data/film/_bulk?pretty" --data-binary @news_data.json
     *
     *
     * curl -H "Content-Type: application/x-ndjson" -XPOST "203.93.173.179:9200/news_data/news/_bulk?pretty" --data-binary @news_data.json
     * curl -H "Content-Type: application/x-ndjson" -XPOST "203.93.173.179:9200/chapter_data/chapter/_bulk?pretty" --data-binary @chapter.json
     * curl -H "Content-Type: application/x-ndjson" -XPOST "203.93.173.179:9200/book_info/book/_bulk?pretty" --data-binary @book_info.json
     * curl -H "Content-Type: application/x-ndjson" -XPOST "203.93.173.179:9200/search_text/text/_bulk?pretty" --data-binary @search_text.json
     */
    @Test
    public void test() {
//        String indexName = "news_data";
//        String typeName = "news";
//        String toIndexName = "news_data";
//        String fileName = "/home/hadoop/export_es/news_data.json";


//        String indexName = "chapter_data";
//        String typeName = "chapter";
//        String toIndexName = "chapter_data";
//        String fileName = "/home/hadoop/export_es/chapter.json";


//        String indexName = "book_info";
//        String typeName = "book";
//        String toIndexName = "book_info";
//        String fileName = "/home/hadoop/export_es/book_info.json";


        String indexName = "search_text";
        String typeName = "text";
        String toIndexName = "search_text";
        String fileName = "/home/hadoop/export_es/search_text.json";

        SearchResponse response = client.prepareSearch(indexName).setTypes(typeName).setQuery(QueryBuilders.
                matchAllQuery()).execute().actionGet();
        SearchHits resultHits = response.getHits();

        Long totalHits = resultHits.totalHits;
        response = client.prepareSearch(indexName)
                .setTypes(typeName).setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(totalHits.intValue())
                .execute().actionGet();
        resultHits = response.getHits();

        StringBuilder stringBuilder = new StringBuilder(128214);
        for (int i = 0; i < resultHits.getHits().length; i++) {
            String jsonStr = resultHits.getHits()[i].getSourceAsString();

            String index = "{\"index\":{\"_index\":\"" + toIndexName + "\",\"_id\":" + i + "}}\n";
            stringBuilder.append(index);
            stringBuilder.append(jsonStr).append("\n");
        }
        FileUtils.writeStrToFile(stringBuilder.toString(), fileName);

    }
}
