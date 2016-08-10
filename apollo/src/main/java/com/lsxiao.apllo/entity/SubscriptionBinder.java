package com.lsxiao.apllo.entity;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * author lsxiao
 * date 2016-08-08 14:18
 */
public class SubscriptionBinder {
    private CompositeSubscription mSubscription;

    public SubscriptionBinder() {
        mSubscription = new CompositeSubscription();
    }

    public void add(Subscription subscription) {
        if (mSubscription == null) {
            throw new IllegalAccessError("this binder has been unbinded");
        }
        if (subscription == null) {
            throw new NullPointerException("subscription must be not null");
        }
        mSubscription.add(subscription);
    }

    public void unbind() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    public boolean isUnbind() {
        return mSubscription == null;
    }
}
