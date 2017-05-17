package com.lsxiao.apollo.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.demo.model.User;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-15 15:18
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class TestService extends Service {
    public TestService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Apollo.emit("ipc", new User("a User from TestService and pid is" + Process.myPid()));
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
