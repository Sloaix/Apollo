package com.lsxiao.apollo.demo.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.apollo.core.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseFragment;
import com.lsxiao.apollo.demo.constant.Event;
import com.lsxiao.apollo.demo.model.User;

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

    @Receive(Event.DOBLUE_NUMBER)
    public void onEvent(double value) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + value + ",");
    }

    @Receive(Event.FLOAT_NUMBER)
    public void onEvent(float value) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + value + ",");
    }

    @Receive(Event.INT_NUMBER)
    public void onEvent(int value) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + value + ",");
    }


    @Receive(Event.OBJECT)
    public void onEvent(User value) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + value + ",");
    }


    @Receive(Event.BOOL)
    public void onEvent(boolean value) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + value + ",");
    }

    @Receive(Event.STR)
    public void onEvent(String value) {
        mTvReceiveEvent.setText(mTvReceiveEvent.getText().toString() + value + ",");
    }
}
