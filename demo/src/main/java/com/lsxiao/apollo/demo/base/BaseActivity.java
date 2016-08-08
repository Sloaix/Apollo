package com.lsxiao.apollo.demo.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.entity.SubscriptionBinder;


public abstract class BaseActivity extends AppCompatActivity {
    private SubscriptionBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        afterCreate(savedInstanceState);
        mBinder = Apollo.get().bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}
