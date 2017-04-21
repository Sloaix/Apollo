package com.lsxiao.apollo.demo;

import android.app.Application;

//import com.lsxiao.apollo.generate.ApolloBinderGeneratorImpl;


/**
 * author lsxiao
 * date 2016-08-08 13:33
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Apollo.get().init(ApolloBinderGeneratorImpl.instance(), SchedulerProvider.create(AndroidSchedulers.mainThread()));
    }
}
