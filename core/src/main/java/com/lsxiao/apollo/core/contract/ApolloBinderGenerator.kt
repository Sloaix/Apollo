package com.lsxiao.apollo.core.contract

import com.lsxiao.apollo.core.entity.Event

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:21
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

interface ApolloBinderGenerator {
    fun generate(subscriber: Any): ApolloBinder

    fun broadcastEvent(event: Event)

    fun registerReceiver()
}
