package com.lsxiao.apollo.demo;

import android.app.Application;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.entity.SchedulerProvider;
import com.lsxiao.apollo.generate.ApolloBinderGeneratorImpl;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * author lsxiao
 * date 2016-08-08 13:33
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Apollo.get().init(ApolloBinderGeneratorImpl.instance(), SchedulerProvider.create(AndroidSchedulers.mainThread()));
    }
}
