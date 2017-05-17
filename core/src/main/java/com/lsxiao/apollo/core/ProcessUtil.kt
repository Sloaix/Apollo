package com.lsxiao.apollo.core

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-15 16:56
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

object ProcessUtil {
    /**
     * 使用反射的方式获取进程的id(Apollo是纯java库)
     */
    fun getPid(): Int {
        val threadClazz = Class.forName("android.os.Process")
        val method = threadClazz.getMethod("myPid")
        return method.invoke(null) as Int
    }
}