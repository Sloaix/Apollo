package com.lsxiao.apllo.processor

object Utils {
    fun split(list: List<String>, separator: String): String {
        return list.mapTo(ArrayList<String>()) { "\"$it\"" }.joinToString(separator)
    }
}