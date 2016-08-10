package com.lsxiao.apollo.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String EVENT_USER = "event_user";
    private TextView mTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTextView = (TextView) findViewById(R.id.tv);
        findViewById(R.id.bt_send_event_to_fir_act).setOnClickListener(this);
        findViewById(R.id.bt_send_sticky_event_to_sec_act).setOnClickListener(this);
        findViewById(R.id.bt_start_sec_act).setOnClickListener(this);
        findViewById(R.id.bt_remove_sec_act_sticky_event).setOnClickListener(this);
    }

    @Receive(tag = EVENT_USER)
    public void receiveUser(User user) {
        mTextView.setText(user.toString());
        Toast.makeText(this, "MainActivity receive user event" + user.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_event_to_fir_act: {
                Apollo.get().send(EVENT_USER, new User("lsxiao"));
                break;
            }
            case R.id.bt_send_sticky_event_to_sec_act: {
                Apollo.get().sendSticky(SecondActivity.EVENT_BOOK, new SecondActivity.Book("A Song of Ice and Fire"));
                break;
            }
            case R.id.bt_start_sec_act: {
                final Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.bt_remove_sec_act_sticky_event: {
                Apollo.get().removeStickyEvent(SecondActivity.EVENT_BOOK);
                break;
            }
        }
    }

    public static class User {
        String name;

        public User(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
