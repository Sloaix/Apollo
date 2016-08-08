package com.lsxiao.apollo.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String EVENT_SHOW_USER = "event_show_user";
    public static final String EVENT_SHOW_BOOK = "event_show_book";
    private TextView mTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTextView = (TextView) findViewById(R.id.tv);
        findViewById(R.id.bt_send_event).setOnClickListener(this);
        findViewById(R.id.bt_send_sticky_event).setOnClickListener(this);
        findViewById(R.id.bt_show_book_activity).setOnClickListener(this);
        findViewById(R.id.bt_clear_sticky_event).setOnClickListener(this);
    }

    @Receive(tag = EVENT_SHOW_BOOK)
    public void receiveBook(Book book) {
        Log.d("apollo", "MainActivity receive book event" + book.toString());
        mTextView.setText(book.toString());
    }

    @Receive(tag = EVENT_SHOW_USER)
    public void receiveUser(User user) {
        Log.d("apollo", "MainActivity receive user event" + user.toString());
        mTextView.setText(user.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_event: {
                Apollo.get().send(EVENT_SHOW_USER, new User("lsxiao"));
                break;
            }
            case R.id.bt_send_sticky_event: {
                Apollo.get().sendSticky(EVENT_SHOW_BOOK, new Book("A Song of Ice and Fire"));
                break;
            }
            case R.id.bt_show_book_activity: {
                final Intent intent = new Intent(this, BookActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.bt_clear_sticky_event: {
                Apollo.get().removeStickyEvent(EVENT_SHOW_BOOK);
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

    public static class Book {
        String name;

        public Book(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
