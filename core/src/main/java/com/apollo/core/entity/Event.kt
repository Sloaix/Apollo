package com.apollo.core.entity

import java.io.Serializable

/**
 * author lsxiao
 * date 2016-08-07 20:00
 */
class Event constructor(val tag: String, val data: Any, val pid: Int, val isSticky: Boolean = false) : Serializable
