package com.lsxiao.apollo.demo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseActivity;
import com.lsxiao.apollo.demo.fragment.ProducerFragment;
import com.lsxiao.apollo.demo.fragment.SubscriberFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        final Fragment subscriberFragment = SubscriberFragment.newInstance();
        final Fragment producerFragment = ProducerFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_subscriber, subscriberFragment, SubscriberFragment.TAG)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_producer, producerFragment, SubscriberFragment.TAG)
                .commit();
    }
}
