package com.lsxiao.apllo.processor.step

import com.lsxiao.apollo.core.annotations.Receive
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.lsxiao.apllo.processor.ApolloDescriptor
import com.lsxiao.apllo.processor.ApolloProcessor
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:17
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class ReceiveStep : BasicAnnotationProcessor.ProcessingStep {
    override fun annotations(): Set<Class<out Annotation>> = setOf(Receive::class.java)

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        elementsByAnnotation.asMap().keys.forEach {
            elementsByAnnotation.asMap()[it]?.forEach { element ->
                val descriptor = ApolloDescriptor.newInstance(element as ExecutableElement)
                descriptor.tags = Arrays.asList<String>(*MoreElements.asExecutable(element).getAnnotation(Receive::class.java).value)
                ApolloProcessor.sDescriptorMap.put(element, descriptor)
            }
        }
        return HashSet()
    }
}
