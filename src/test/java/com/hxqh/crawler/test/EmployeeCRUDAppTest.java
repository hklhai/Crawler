package com.hxqh.crawler.test;

import org.elasticsearch.client.transport.TransportClient;

/**
 * Created by Ocean lin on 2017/10/16.
 */
public class EmployeeCRUDAppTest {

    private TransportClient client;

//    @Before
//    public void init() throws Exception {
//        Settings settings = Settings.builder()
//                .put("cluster.name", "elasticsearch")
//                .build();
//
//        client = new PreBuiltTransportClient(settings)
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//
////		createEmployee(client);
////		getEmployee(client);
////		updateEmployee(client);
////		deleteEmployee(client);
//
////        client.close();
//    }

//    /**
//     * 创建员工信息（创建一个document）
//     */
//    @Test
//    public void createEmployee() throws Exception {
//        IndexResponse response = client.prepareIndex("company", "employee", "1")
//                .setSource(XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("name", "jack")
//                        .field("age", 27)
//                        .field("position", "technique")
//                        .field("country", "china")
//                        .field("join_date", "2017-01-01")
//                        .field("salary", 10000)
//                        .endObject())
//                .get();
//        System.out.println(response.getResult());
//        client.close();
//    }
//
//    /**
//     * 获取员工信息
//     *
//     * @throws Exception
//     */
//    @Test
//    public void getEmployee() throws Exception {
//        GetResponse response = client.prepareGet("company", "employee", "1").get();
//        System.out.println(response.getSourceAsString());
//    }
//
//
//    /**
//     * 修改员工信息
//     *
//     * @throws Exception
//     */
//    @Test
//    public void updateEmployee() throws Exception {
//        UpdateResponse response = client.prepareUpdate("company", "employee", "1")
//                .setDoc(XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("position", "technique manager")
//                        .endObject())
//                .get();
//        System.out.println(response.getResult());
//    }
//
//
//    /**
//     * 删除 员工信息
//     *
//     * @throws Exception
//     */
//    @Test
//    public void deleteEmployee() throws Exception {
//        DeleteResponse response = client.prepareDelete("company", "employee", "1").get();
//        System.out.println(response.getResult());
//    }

}
