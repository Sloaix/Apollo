package com.lsxiao.apollo.core.annotations

/**
 * author lsxiao
 * date 2016-08-07 18:14
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention
annotation class Receive(vararg val value: String)
