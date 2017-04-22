package com.lsxiao.apllo.processor.step;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.apollo.core.annotations.SubscribeOn;
import com.apollo.core.entity.SchedulerProvider;
import com.lsxiao.apllo.processor.ApolloDescriptor;
import com.lsxiao.apllo.processor.ApolloProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:17
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class SubscribeStep implements BasicAnnotationProcessor.ProcessingStep {
    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(
                SubscribeOn.class
        );
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Class<? extends Annotation> cls : elementsByAnnotation.asMap().keySet()) {
            Collection<Element> elements = elementsByAnnotation.asMap().get(cls);

            for (Element element : elements) {
                ApolloDescriptor descriptor = ApolloProcessor.sDescriptorMap.get(element);
                if (descriptor == null) {
                    continue;
                }

                if (MoreElements.isAnnotationPresent(element, SubscribeOn.class)) {
                    SchedulerProvider.Tag tag = MoreElements.asExecutable(element).getAnnotation(SubscribeOn.class).value();
                    descriptor.subscribe(tag);
                }
            }
        }
        return new HashSet<>();
    }
}
