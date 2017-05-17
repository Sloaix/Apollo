package com.lsxiao.apllo.processor.step

import com.lsxiao.apollo.core.annotations.ObserveOn
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

class ObserveStep : BasicAnnotationProcessor.ProcessingStep {
    override fun annotations(): Set<Class<out Annotation>> = setOf(ObserveOn::class.java)

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        elementsByAnnotation.asMap().keys.forEach {
            elementsByAnnotation.asMap()[it]?.forEach list@ {
                val descriptor = ApolloProcessor.sDescriptorMap[it] ?: return@list

                if (MoreElements.isAnnotationPresent(it, ObserveOn::class.java)) {
                    descriptor.observeOn = MoreElements.asExecutable(it).getAnnotation(ObserveOn::class.java).value
                }
            }
        }
        return HashSet()
    }
}
