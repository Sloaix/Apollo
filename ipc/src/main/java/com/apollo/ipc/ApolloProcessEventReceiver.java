package com.apollo.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.apollo.core.Apollo;
import com.apollo.core.entity.Event;

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
        if (!intent.hasExtra(KEY_EVENT) || intent.getSerializableExtra(KEY_EVENT) == null || (!(intent.getSerializableExtra(KEY_EVENT) instanceof Event))) {
            return;
        }

        Event event = (Event) intent.getSerializableExtra(KEY_EVENT);

        if (event.getPid() == Process.myPid()) {
            return;
        }

        Apollo.emit(event);
    }
    
}
