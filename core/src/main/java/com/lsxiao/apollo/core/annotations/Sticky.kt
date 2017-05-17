package com.lsxiao.apollo.core.annotations

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:20
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention
annotation class Sticky(val remove: Boolean = true)
