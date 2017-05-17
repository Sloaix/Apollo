package com.lsxiao.apollo.core.entity

import com.lsxiao.apollo.core.contract.ApolloBinder

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:27
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class ApolloBinderImpl : ApolloBinder {
    val mCompositeDisposable: io.reactivex.disposables.CompositeDisposable by lazy {
        io.reactivex.disposables.CompositeDisposable()
    }

    override fun add(disposable: io.reactivex.disposables.Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun unbind() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.clear()
        }
    }

    override fun isUnbind(): Boolean {
        return mCompositeDisposable.isDisposed
    }

}
