package com.lsxiao.apllo.entity;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:32
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class SchedulerProvider {
    private Scheduler mMain;
    private Scheduler mIO = Schedulers.io();
    private Scheduler mComputation = Schedulers.computation();
    private Scheduler mTrampoline = Schedulers.trampoline();
    private Scheduler mSingle = Schedulers.single();
    private Scheduler mNew = Schedulers.newThread();


    public enum Tag {
        MAIN, IO, NEW, COMPUTATION, TRAMPOLINE, SINGLE
    }

    private SchedulerProvider(Scheduler main) {
        mMain = main;
    }

    public static SchedulerProvider create(Scheduler main) {
        if (null == main) {
            throw new NullPointerException("the scheduler main must be not null");
        }

        return new SchedulerProvider(main);
    }

    public Scheduler getMain() {
        return mMain;
    }

    public Scheduler getIO() {
        return mIO;
    }

    public Scheduler getComputation() {
        return mComputation;
    }

    public Scheduler getTrampoline() {
        return mTrampoline;
    }

    public Scheduler getSingle() {
        return mSingle;
    }

    public Scheduler getNew() {
        return mNew;
    }

    public Scheduler get(Tag tag) {
        switch (tag) {
            case MAIN: {
                return getMain();
            }
            case IO: {
                return getIO();
            }
            case COMPUTATION: {
                return getComputation();
            }
            case TRAMPOLINE: {
                return getTrampoline();
            }
            case SINGLE: {
                return getSingle();
            }
            case NEW: {
                return getNew();
            }
            default: {
                return getMain();
            }
        }
    }
}
