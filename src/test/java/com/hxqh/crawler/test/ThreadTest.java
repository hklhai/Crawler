package com.hxqh.crawler.test;

import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ocean lin on 2018/1/18.
 */
public class ThreadTest {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<List<Integer>> partition = ListUtils.partition(list, 3);

        ExecutorService service = Executors.newFixedThreadPool(5);


        for (int i = 0; i < partition.size(); i++) {
            service.execute(new Process(partition.get(i)));
        }
        service.shutdown();

        while (!service.isTerminated()) {
        }
    }


}

class Process implements Runnable {

    private List<Integer> list;

    public Process(List<Integer> list) {
        this.list = list;
    }

    @Override
    public void run() {
        for (int i = 0; i < list.size(); i++) {
            try {
                if (list.get(i) % 2 == 0) {
                    int x = list.get(i) / 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(list.get(i));
        }
    }
}

