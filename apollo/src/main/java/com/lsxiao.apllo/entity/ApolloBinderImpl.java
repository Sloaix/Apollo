package com.lsxiao.apllo.entity;

import com.lsxiao.apllo.contract.ApolloBinder;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:27
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class ApolloBinderImpl implements ApolloBinder<Disposable> {
    private CompositeDisposable mCompositeDisposable;

    public ApolloBinderImpl() {
        mCompositeDisposable = new CompositeDisposable();
    }


    public void add(Disposable disposable) {
        if (mCompositeDisposable == null) {
            throw new IllegalAccessError("this binder has been unbinded");
        }
        if (disposable == null) {
            throw new NullPointerException("disposable must be not null");
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void clearAll() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    @Override
    public boolean isUnbind() {
        return mCompositeDisposable == null;
    }


}
