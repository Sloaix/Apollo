package com.lsxiao.apllo.processor.step

import com.lsxiao.apollo.core.annotations.Backpressure
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.lsxiao.apllo.processor.ApolloProcessor
import io.reactivex.BackpressureStrategy
import java.util.*
import javax.lang.model.element.Element

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:17
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class BackpressureStep : BasicAnnotationProcessor.ProcessingStep {
    override fun annotations(): Set<Class<out Annotation>> = setOf(Backpressure::class.java)

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        elementsByAnnotation.asMap().keys.forEach { clazz ->
            elementsByAnnotation.asMap()[clazz]?.forEach list@ { element ->
                val descriptor = ApolloProcessor.sDescriptorMap[element] ?: return@list

                if (MoreElements.isAnnotationPresent(element, Backpressure::class.java)) {
                    MoreElements.asExecutable(element).getAnnotation(Backpressure::class.java).value.takeIf {
                        it == BackpressureStrategy.BUFFER || it == BackpressureStrategy.DROP || it == BackpressureStrategy.LATEST
                    }.let {
                        if (it != null) {
                            descriptor.backpressureStrategy = it
                        }
                    }
                }
            }
        }
        return HashSet()
    }
}
