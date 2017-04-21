package com.lsxiao.apllo.annotations;

import com.lsxiao.apllo.entity.SchedulerProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:21
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface SubscribeOn {
    SchedulerProvider.Tag value() default SchedulerProvider.Tag.IO;
}
