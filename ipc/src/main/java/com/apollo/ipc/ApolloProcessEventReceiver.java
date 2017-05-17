package com.apollo.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.apollo.core.Apollo;
import com.apollo.core.entity.Event;

import org.nustaq.serialization.FSTConfiguration;

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
        FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

        Event event = (Event) conf.asObject(intent.getByteArrayExtra(KEY_EVENT));

        if (event.getPid() == Process.myPid()) {
            return;
        }

        Apollo.transfer(event);
    }

}
