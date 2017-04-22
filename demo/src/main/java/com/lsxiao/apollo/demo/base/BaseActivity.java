package com.lsxiao.apollo.demo.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.apollo.core.Apollo;
import com.apollo.core.contract.ApolloBinder;


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
        mBinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}
