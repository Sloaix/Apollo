package com.apollo.core.entity

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
    val io = Schedulers.io()!!
    val computation = Schedulers.computation()!!
    val trampoline = Schedulers.trampoline()!!
    val single = Schedulers.single()!!
    val new = Schedulers.newThread()!!


    enum class Tag {
        MAIN, IO, NEW, COMPUTATION, TRAMPOLINE, SINGLE
    }

    operator fun get(tag: Tag): Scheduler {
        when (tag) {
            SchedulerProvider.Tag.MAIN -> return main
            SchedulerProvider.Tag.IO -> return io
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
