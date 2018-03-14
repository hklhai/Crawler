package com.hxqh.crawler.util;

import java.util.regex.Pattern;

/**
 * Created by Ocean lin on 2018/2/6.
 *
 * @author Ocean lin
 */
public class NumUtils {

    public static Double getNumber(String stringNum) {
        Double v = null;
        if (stringNum.endsWith("万")) {
            stringNum = stringNum.substring(0, stringNum.length() - 1);
            v = Double.valueOf(stringNum);
        } else if (stringNum.endsWith("亿")) {
            stringNum = stringNum.substring(0, stringNum.length() - 1);
            v = Double.valueOf(stringNum);
        } else {
            v = Double.valueOf(stringNum);
        }
        return v;
    }

    public static Integer getReleaseInfo(String releaseInfo) {
        Integer integer = null;
        String s = releaseInfo.replaceAll("上映", "").replaceAll("天", "");
        if (s.equals("首日")) {
            integer = 1;
        } else {
            integer = Integer.valueOf(s);
        }
        return integer;
    }

    /**
     * 判断整数（int）
     *
     * @param str 输入字符串
     * @return 是否整数
     */
    public static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    /**
     * 判断浮点数（double和float）
     *
     * @param str 输入字符串
     * @return 是否浮点数
     */
    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
