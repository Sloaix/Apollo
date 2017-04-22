package com.apollo.core.contract

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:21
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

interface ApolloBinderGenerator {
    fun generate(subscriber: Any): ApolloBinder
}
