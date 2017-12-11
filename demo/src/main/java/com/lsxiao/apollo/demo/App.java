package com.lsxiao.apollo.demo;

import android.app.Application;

import com.lsxiao.apollo.core.Apollo;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * author lsxiao
 * date 2016-08-08 13:33
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Apollo.init(AndroidSchedulers.mainThread(), this, true);
    }
}
