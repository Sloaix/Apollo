package com.lsxiao.apollo.demo.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.apollo.core.Apollo;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseFragment;

/**
 * author lsxiao
 * date 2016-08-23 19:29
 */
public class ProducerFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "ProducerFragment";
    private TextView mTvSentEvent;
    private TextView mTvSentStickyEvent;
    private SubscriberDialogFragment mSubscriberDialogFragment;

    public static ProducerFragment newInstance() {
        Bundle args = new Bundle();
        ProducerFragment fragment = new ProducerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_producer;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mRootView.findViewById(R.id.btn_send_event).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_send_sticky_event).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_show_dialog).setOnClickListener(this);
        mTvSentEvent = (TextView) mRootView.findViewById(R.id.tv_sent_event);
        mTvSentStickyEvent = (TextView) mRootView.findViewById(R.id.tv_sent_sticky_event);
        mSubscriberDialogFragment = SubscriberDialogFragment.newInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_event: {
                Apollo.get().send("event", "event");
                mTvSentEvent.setText(String.format("%sevent,", mTvSentEvent.getText().toString()));
                break;
            }
            case R.id.btn_send_sticky_event: {
                Apollo.get().sendSticky("sticky", "sticky");
                mTvSentStickyEvent.setText(String.format("%ssticky,", mTvSentStickyEvent.getText().toString()));
                break;
            }
            case R.id.btn_show_dialog: {
                if (!mSubscriberDialogFragment.isAdded()) {
                    mSubscriberDialogFragment = SubscriberDialogFragment.newInstance();
                }
                mSubscriberDialogFragment.show(getChildFragmentManager(), SubscriberDialogFragment.TAG);
                break;
            }
        }
    }
}
