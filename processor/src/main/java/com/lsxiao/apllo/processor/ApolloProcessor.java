package com.lsxiao.apllo.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.lsxiao.apllo.processor.step.BackpressureStep;
import com.lsxiao.apllo.processor.step.ObserveStep;
import com.lsxiao.apllo.processor.step.TakeStep;
import com.lsxiao.apllo.processor.step.ReceiveStep;
import com.lsxiao.apllo.processor.step.StickyStep;
import com.lsxiao.apllo.processor.step.SubscribeStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;


@AutoService(Processor.class)
public class ApolloProcessor extends BasicAnnotationProcessor {
    public static Map<Element, ApolloDescriptor> sDescriptorMap = new HashMap<>();
    private boolean mGenerated = false;

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ReceiveStep(),
                new TakeStep(),
                new StickyStep(),
                new BackpressureStep(),
                new SubscribeStep(),
                new ObserveStep()
        );
    }

    @Override
    protected void postRound(RoundEnvironment roundEnv) {
        super.postRound(roundEnv);
        if (mGenerated) {
            return;
        }
        CodeGenerator.Companion.create(new ArrayList<>(sDescriptorMap.values()), processingEnv.getFiler()).generate();
        mGenerated = true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
