package com.apollo.core.entity

import java.io.Serializable
import kotlin.properties.Delegates

/**
 * author lsxiao
 * date 2016-08-07 20:00
 */
class Event constructor() : Serializable {
    constructor(tag: String, data: Any, pid: Int, isSticky: Boolean = false) : this() {
        this.tag = tag
        this.data = data
        this.pid = pid
        this.isSticky = isSticky
    }

    var tag: String by Delegates.notNull()
    var data: Any by Delegates.notNull()
    var pid: Int by Delegates.notNull()
    var isSticky: Boolean by Delegates.notNull()
}
