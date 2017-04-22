package com.lsxiao.apllo.processor

import com.apollo.core.entity.SchedulerProvider
import io.reactivex.BackpressureStrategy
import java.util.*
import javax.lang.model.element.ExecutableElement

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:19
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class ApolloDescriptor private constructor(val methodElement: ExecutableElement) {
    companion object {
        fun newInstance(methodElement: ExecutableElement): ApolloDescriptor {
            return ApolloDescriptor(methodElement)
        }
    }

    var isSticky = false
    var tags: List<String> = ArrayList()
    var subscribeOn: SchedulerProvider.Tag = SchedulerProvider.Tag.IO
    var observeOn: SchedulerProvider.Tag = SchedulerProvider.Tag.MAIN
    var backpressureStrategy: BackpressureStrategy? = null

    fun sticky(value: Boolean): ApolloDescriptor {
        isSticky = value
        return this
    }

    fun tags(value: List<String>): ApolloDescriptor {
        tags = value
        return this
    }

    fun subscribe(value: SchedulerProvider.Tag): ApolloDescriptor {
        subscribeOn = value
        return this
    }

    fun observe(value: SchedulerProvider.Tag): ApolloDescriptor {
        observeOn = value
        return this
    }

    fun backpressure(value: BackpressureStrategy): ApolloDescriptor {
        backpressureStrategy = value
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as ApolloDescriptor?

        return methodElement == that!!.methodElement

    }

    override fun hashCode(): Int {
        return methodElement.hashCode()
    }

}
