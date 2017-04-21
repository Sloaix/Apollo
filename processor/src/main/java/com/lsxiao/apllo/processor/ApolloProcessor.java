package com.lsxiao.apllo.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.lsxiao.apllo.processor.step.BackpressureStep;
import com.lsxiao.apllo.processor.step.ObserveStep;
import com.lsxiao.apllo.processor.step.ReceiveStep;
import com.lsxiao.apllo.processor.step.StickyStep;
import com.lsxiao.apllo.processor.step.SubscribeStep;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;


@AutoService(Processor.class)
public class ApolloProcessor extends BasicAnnotationProcessor {
    public static Map<Element, ApolloDescriptor> sDescriptorMap = new HashMap<>();

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ReceiveStep(),
                new StickyStep(),
                new BackpressureStep(),
                new SubscribeStep(),
                new ObserveStep()
        );
    }

    /**
     * 指定使用的java版本。通常这里会直接放回SourceVersion.latestSupported()即可。
     *
     * @return SourceVersion
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
