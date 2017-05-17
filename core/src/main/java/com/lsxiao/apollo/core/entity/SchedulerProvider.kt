package com.lsxiao.apollo.core.entity

import io.reactivex.Scheduler


/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:32
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class SchedulerProvider private constructor(private val main: Scheduler) {
    val inout = io.reactivex.schedulers.Schedulers.io()!!
    val computation = io.reactivex.schedulers.Schedulers.computation()!!
    val trampoline = io.reactivex.schedulers.Schedulers.trampoline()!!
    val single = io.reactivex.schedulers.Schedulers.single()!!
    val new = io.reactivex.schedulers.Schedulers.newThread()!!


    enum class Tag {
        MAIN, IO, NEW, COMPUTATION, TRAMPOLINE, SINGLE
    }

    operator fun get(tag: SchedulerProvider.Tag): Scheduler {
        when (tag) {
            SchedulerProvider.Tag.MAIN -> return main
            SchedulerProvider.Tag.IO -> return inout
            SchedulerProvider.Tag.COMPUTATION -> return computation
            SchedulerProvider.Tag.TRAMPOLINE -> return trampoline
            SchedulerProvider.Tag.SINGLE -> return single
            SchedulerProvider.Tag.NEW -> return new
            else -> return main
        }
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
