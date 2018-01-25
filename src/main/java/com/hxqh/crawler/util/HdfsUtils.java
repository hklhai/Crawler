package com.hxqh.crawler.util;

import com.hxqh.crawler.common.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Ocean lin on 2018/1/25.
 */
public class HdfsUtils {


    public static void persistToHDFS(String paltform, String loc) throws URISyntaxException, IOException {
        Configuration conf = new Configuration();
        URI uri = new URI(Constants.HDFS_URL);
        FileSystem fs = FileSystem.get(uri, conf);
        String path = Constants.SAVE_PATH + Constants.FILE_SPLIT + DateUtils.getTodayDate() + paltform;
        Path resP = new Path(path);
        String location = loc + Constants.FILE_SPLIT +
                DateUtils.getTodayYear() + Constants.FILE_SPLIT + DateUtils.getTodayMonth();
        Path destP = new Path(location);
        if (!fs.exists(destP)) {
            fs.mkdirs(destP);
        }
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        fs.copyFromLocalFile(resP, destP);
        System.out.println("upload file " + name + " to HDFS");
        fs.close();
    }
}
