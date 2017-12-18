package com.lsxiao.apollo.core.entity

import com.lsxiao.apollo.core.entity.SchedulerProvider.Tag.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:32
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class SchedulerProvider private constructor(private val main: Scheduler) {
    private val inout = Schedulers.io()
    private val computation = Schedulers.computation()
    private val trampoline = Schedulers.trampoline()
    private val single = Schedulers.single()
    private val new = Schedulers.newThread()


    enum class Tag {
        MAIN, IO, NEW, COMPUTATION, TRAMPOLINE, SINGLE
    }

    operator fun get(tag: SchedulerProvider.Tag): Scheduler = when (tag) {
        MAIN -> main
        IO -> inout
        COMPUTATION -> computation
        TRAMPOLINE -> trampoline
        SINGLE -> single
        NEW -> new
    }

    companion object {

        fun create(main: Scheduler?): SchedulerProvider {
            if (null == main) {
                throw NullPointerException("the scheduler main must be not null")
            }

            return SchedulerProvider(main)
        }
    }
}
