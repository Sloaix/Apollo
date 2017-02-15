package com.lsxiao.apllo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author lsxiao
 * date 2016-08-07 18:14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Receive {
    String[] tag();

    Type type() default Type.NORMAL;

    Thread subscribeOn() default Thread.IO;

    Thread observeOn() default Thread.MAIN;

    enum Type {
        NORMAL, STICKY, NORMAL_ONCE, STICKY_REMOVE, STICKY_REMOVE_ALL
    }

    enum Thread {
        MAIN, IO, NEW, COMPUTATION, TRAMPOLINE, SINGLE
    }

}
