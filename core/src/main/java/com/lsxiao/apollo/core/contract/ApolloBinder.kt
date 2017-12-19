package com.lsxiao.apollo.core.contract

import io.reactivex.disposables.Disposable

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:23
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

interface ApolloBinder {
    fun add(disposable: Disposable)

    fun unbind()

    fun isUnbind(): Boolean
}
