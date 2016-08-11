package com.lsxiao.apllo.processor.util;

import java.util.Arrays;
import java.util.List;

/**
 * author lsxiao
 * date 2016-08-07 17:48
 */
public class StrUtil {
    private StrUtil() {
    }

    public static String dot2Underline(String s) {
        return s.replace(".", "_");
    }

    public static String arraySplitBy(String[] strings, String separator) {
        String temp = "";
        final List<String> list = Arrays.asList(strings);
        for (String s : list) {
            if (list.indexOf(s) + 1 == list.size()) {
                temp += wrapBy(s, "\"");
            } else {
                temp += wrapBy(s, "\"") + separator;
            }
        }
        return temp;
    }

    public static String wrapBy(String s, String wrapper) {
        return wrapper + s + wrapper;
    }
}
