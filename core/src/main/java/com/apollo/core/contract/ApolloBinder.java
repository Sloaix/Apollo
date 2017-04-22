package com.apollo.core.contract;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-02-15 22:23
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public interface ApolloBinder<T> {
    public void add(T t);

    public void unbind();

    public boolean isUnbind();
}
