package com.lsxiao.apllo.annotations;

import io.reactivex.BackpressureStrategy;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 03:00
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public @interface Backpressure {
    BackpressureStrategy value() ;
}
