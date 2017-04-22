package com.lsxiao.apllo.processor.step;

import com.apollo.core.annotations.Receive;
import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.lsxiao.apllo.processor.ApolloDescriptor;
import com.lsxiao.apllo.processor.ApolloProcessor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:17
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class ReceiveStep implements BasicAnnotationProcessor.ProcessingStep {
    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(
                Receive.class
        );
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Class<? extends Annotation> cls : elementsByAnnotation.asMap().keySet()) {
            Collection<Element> elements = elementsByAnnotation.asMap().get(cls);

            for (Element element : elements) {
                ApolloDescriptor descriptor = ApolloDescriptor.newInstance((ExecutableElement) element);
                descriptor.tags(Arrays.asList(MoreElements.asExecutable(element).getAnnotation(Receive.class).value()));
                ApolloProcessor.sDescriptorMap.put(element, descriptor);
            }
        }
        return new HashSet<>();
    }
}
