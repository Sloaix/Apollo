package com.lsxiao.apollo.core.entity

import com.lsxiao.apollo.core.Apollo
import com.lsxiao.apollo.core.contract.ApolloBinder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:27
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class ApolloBinderImpl(private val o: Any) : ApolloBinder {
    private val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun add(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun unbind() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.clear()
        }
        Apollo.unBind(o)
    }

    override fun isUnbind(): Boolean {
        return mCompositeDisposable.isDisposed
    }

}
