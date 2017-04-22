package com.lsxiao.apllo.processor

object Utils {
    fun arraySplitBy(list: List<String>, separator: String): String {
        var temp = ""
        list.forEach { s ->
            when {
                list.indexOf(s) + 1 != list.size -> temp += wrapBy(s, "\"") + separator
                else -> temp += wrapBy(s, "\"")
            }
        }
        return temp
    }

    fun wrapBy(s: String, wrapper: String): String = wrapper + s + wrapper
}