package com.lsxiao.apllo.processor.step

import com.lsxiao.apollo.core.annotations.SubscribeOn
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

class SubscribeStep : BasicAnnotationProcessor.ProcessingStep {
    override fun annotations(): Set<Class<out Annotation>> = setOf(SubscribeOn::class.java)

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        elementsByAnnotation.asMap().keys
                .map { elementsByAnnotation.asMap()[it] }
                .forEach { it ->
                    it?.forEach list@ {
                        val descriptor = ApolloProcessor.sDescriptorMap[it] ?: return@list

                        if (MoreElements.isAnnotationPresent(it, SubscribeOn::class.java)) {
                            descriptor.subscribeOn = MoreElements.asExecutable(it).getAnnotation(SubscribeOn::class.java).value
                        }
                    }
                }
        return HashSet()
    }
}
