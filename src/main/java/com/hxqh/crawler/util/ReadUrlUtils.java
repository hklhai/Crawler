package com.hxqh.crawler.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ocean lin on 2018/2/1.
 *
 * @author Ocean lin
 */
public class ReadUrlUtils {

    public static JsonObject getXpath(String requestUrl) {
        String res = "";
        JsonObject object = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            if (200 == urlCon.getResponseCode()) {
                InputStream is = urlCon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);

                String str = null;
                while ((str = br.readLine()) != null) {
                    buffer.append(str);
                }
                br.close();
                isr.close();
                is.close();
                res = buffer.toString();
                JsonParser parse = new JsonParser();
                object = (JsonObject) parse.parse(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }
}
