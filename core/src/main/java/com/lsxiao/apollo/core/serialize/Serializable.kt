package com.lsxiao.apollo.core.serialize

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-17 17:23
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

interface Serializable {
    fun serialize(obj: Any): ByteArray

    fun <T> deserialize(data: ByteArray, clazz: Class<T>): T
}
