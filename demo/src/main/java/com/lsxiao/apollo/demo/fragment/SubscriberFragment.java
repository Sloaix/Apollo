package com.lsxiao.apollo.demo.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseFragment;

/**
 * author lsxiao
 * date 2016-08-23 19:14
 */
public class SubscriberFragment extends BaseFragment {
    public static final String TAG = "SubscriberFragment";
    private TextView mTvReceiveEvent;
    private TextView mTvReceiveStickyEvent;

    public static SubscriberFragment newInstance() {
        Bundle args = new Bundle();
        SubscriberFragment fragment = new SubscriberFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_subscriber;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTvReceiveEvent = (TextView) mRootView.findViewById(R.id.tv_received_event);
        mTvReceiveStickyEvent = (TextView) mRootView.findViewById(R.id.tv_received_sticky_event);
    }

    @Receive(tag = "event")
    public void onReceiveEvent(String event) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + event + ",");
    }

    @Receive(tag = "sticky", type = Receive.Type.STICKY)
    public void onReceiveStickyEvent(String event) {
        mTvReceiveStickyEvent.setText(mTvReceiveStickyEvent.getText().toString() + event + ",");
    }
}
