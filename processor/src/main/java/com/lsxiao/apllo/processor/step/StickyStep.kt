package com.lsxiao.apllo.processor.step

import com.lsxiao.apollo.core.annotations.Sticky
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.lsxiao.apllo.processor.ApolloProcessor
import java.util.*
import javax.lang.model.element.Element

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:17
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class StickyStep : BasicAnnotationProcessor.ProcessingStep {
    override fun annotations(): Set<Class<out Annotation>> = setOf(Sticky::class.java)

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        elementsByAnnotation.asMap().keys.forEach { clazz ->
            elementsByAnnotation.asMap()[clazz]?.mapNotNull { ApolloProcessor.sDescriptorMap[it] }?.forEach {
                it.isSticky = true
                it.stickyAutoRemove = MoreElements.asExecutable(it.methodElement).getAnnotation(Sticky::class.java).remove
            }
        }
        return HashSet()
    }
}
