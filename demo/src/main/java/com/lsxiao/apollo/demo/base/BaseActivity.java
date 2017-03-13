package com.lsxiao.apollo.demo.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.contract.ApolloBinder;


public abstract class BaseActivity extends AppCompatActivity {
    private ApolloBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mBinder = Apollo.get().bind(this);
        afterCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinder.clearAll();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}
