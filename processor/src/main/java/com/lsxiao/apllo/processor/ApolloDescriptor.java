package com.lsxiao.apllo.processor;

import com.lsxiao.apllo.entity.SchedulerProvider;

import java.util.List;

import javax.lang.model.element.ExecutableElement;

import io.reactivex.BackpressureStrategy;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 02:19
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class ApolloDescriptor {
    private ExecutableElement mMethodElement;
    private boolean mIsSticky;
    private List<String> mTags;
    private SchedulerProvider.Tag mSubscribeOn = SchedulerProvider.Tag.IO;
    private SchedulerProvider.Tag mObserveOn = SchedulerProvider.Tag.MAIN;
    private BackpressureStrategy mBackpressureStrategy;

    private ApolloDescriptor(ExecutableElement methodElement) {
        mMethodElement = methodElement;
    }

    public static ApolloDescriptor newInstance(ExecutableElement methodElement) {
        return new ApolloDescriptor(methodElement);
    }

    public ApolloDescriptor sticky(boolean val) {
        mIsSticky = val;
        return this;
    }

    public ApolloDescriptor tags(List<String> val) {
        mTags = val;
        return this;
    }

    public ApolloDescriptor subscribe(SchedulerProvider.Tag val) {
        mSubscribeOn = val;
        return this;
    }

    public ApolloDescriptor observe(SchedulerProvider.Tag val) {
        mObserveOn = val;
        return this;
    }

    public ApolloDescriptor backpressure(BackpressureStrategy val) {
        mBackpressureStrategy = val;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApolloDescriptor that = (ApolloDescriptor) o;

        return mMethodElement.equals(that.mMethodElement);

    }

    @Override
    public int hashCode() {
        return mMethodElement.hashCode();
    }
}
