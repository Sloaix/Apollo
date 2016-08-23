package com.lsxiao.apollo.demo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.entity.SubscriptionBinder;

/**
 * author lsxiao
 * date 2016-08-23 19:15
 */
public abstract class BaseFragment extends Fragment {

    public static final String TAG = BaseFragment.class.getSimpleName();
    protected View mRootView;

    private SubscriptionBinder mBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinder = Apollo.get().bind(this);
        afterCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mBinder) {
            mBinder.unbind();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);

}
