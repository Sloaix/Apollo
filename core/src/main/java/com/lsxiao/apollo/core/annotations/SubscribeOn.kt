package com.lsxiao.apollo.core.annotations

import com.lsxiao.apollo.core.entity.SchedulerProvider


/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:21
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention
annotation class SubscribeOn(val value: SchedulerProvider.Tag = SchedulerProvider.Tag.IO)
