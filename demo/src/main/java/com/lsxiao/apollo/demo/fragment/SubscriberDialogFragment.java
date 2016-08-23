package com.lsxiao.apollo.demo.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseDialogFragment;

/**
 * author lsxiao
 * date 2016-08-23 21:01
 */
public class SubscriberDialogFragment extends BaseDialogFragment {
    public static final String TAG = "SubscriberDialogFragment";
    private TextView mTvSentStickyEvent;

    public static SubscriberDialogFragment newInstance() {

        Bundle args = new Bundle();

        SubscriberDialogFragment fragment = new SubscriberDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_subscriber;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTvSentStickyEvent = (TextView) mRootView.findViewById(R.id.tv_sent_sticky_event);
    }

    @Receive(tag = "sticky", type = Receive.Type.STICKY)
    public void onReceiveStickyEvent(String event) {
        mTvSentStickyEvent.setText(mTvSentStickyEvent.getText().toString() + event + ",");
    }
}
