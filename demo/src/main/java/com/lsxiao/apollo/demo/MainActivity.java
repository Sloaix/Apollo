package com.lsxiao.apollo.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.apollo.core.annotations.Receive;
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
        findViewById(R.id.btn_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestService.class);
                startService(intent);
            }
        });
    }

    @Receive("test")
    public void onEvent(String message) {
        Log.d("xls", message);
    }
}
