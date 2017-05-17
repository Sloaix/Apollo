package com.lsxiao.apollo.core.entity

/**
 * author lsxiao
 * date 2016-08-07 20:00
 */
class Event constructor() : java.io.Serializable {
    constructor(tag: String, data: Any, pid: Int, isSticky: Boolean = false) : this() {
        this.tag = tag
        this.data = data
        this.pid = pid
        this.isSticky = isSticky
    }

    var tag: String by kotlin.properties.Delegates.notNull()
    var data: Any by kotlin.properties.Delegates.notNull()
    var pid: Int by kotlin.properties.Delegates.notNull()
    var isSticky: Boolean by kotlin.properties.Delegates.notNull()
}
