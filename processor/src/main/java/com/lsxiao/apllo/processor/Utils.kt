package com.lsxiao.apllo.processor

object Utils {
    fun arraySplitBy(list: List<String>, separator: String): String {
        var temp = ""
        for (s in list) {
            if (list.indexOf(s) + 1 == list.size) {
                temp += wrapBy(s, "\"")
            } else {
                temp += wrapBy(s, "\"") + separator
            }
        }
        return temp
    }

    fun wrapBy(s: String, wrapper: String): String {
        return wrapper + s + wrapper
    }
}