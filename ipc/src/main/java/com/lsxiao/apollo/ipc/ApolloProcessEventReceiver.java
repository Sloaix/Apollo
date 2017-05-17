package com.lsxiao.apollo.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.entity.Event;


/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-15 14:28
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class ApolloProcessEventReceiver extends BroadcastReceiver {
    public static String KEY_EVENT = "event";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.hasExtra(KEY_EVENT) || intent.getByteArrayExtra(KEY_EVENT) == null) {
            return;
        }
        Event event;
        try {
            event = Apollo.getSerializer().deserialize(intent.getByteArrayExtra(KEY_EVENT), Event.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (event.getPid() == Process.myPid()) {
            return;
        }

        Apollo.transfer(event);
    }

}
