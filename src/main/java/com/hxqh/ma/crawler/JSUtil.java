package com.hxqh.ma.crawler;

import org.xml.sax.SAXException;

import java.io.*;

/**
 * Created by Ocean lin on 2018/1/11.
 */
public class JSUtil {

    // 如果要更换运行环境，请注意exePath最后的phantom.exe需要更改。因为这个只能在window版本上运行。前面的路径名
    // 也需要和exePath里面的保持一致。否则无法调用
    private static String projectPath = System.getProperty("user.dir");
    private static String jsPath = projectPath + File.separator + "huicong.js";
    //private static String exePath = projectPath + File.separator + "phantomjs.exe";
    private static String exePath = "D:\\Program\\phantomjs-1.9.2-windows\\phantomjs.exe";

    public static void main(String[] args) throws IOException, SAXException {

        // 测试调用。传入url即可
        String html = getParseredHtml2("http://www.iqiyi.com/v_19rrdwvld8.html#vfrm=19-9-0-1");
        System.out.println("html: " + html);
    }

    // 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。
    public static String getParseredHtml2(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(exePath + " " + jsPath + " " + url);
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp);
        }
        String[] result = sbf.toString().split("companyServiceMod");
        String result2 = "";
        if (result.length >= 2) {
            result2 = result[1];
            if (result2.length() > 200) {
                result2 = result2.substring(0, 200);
            }
        }
        //System.out.println("resut2: "+result2);
        return result2;
    }

}
