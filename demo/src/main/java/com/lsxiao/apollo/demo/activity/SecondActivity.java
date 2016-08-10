package com.lsxiao.apollo.demo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseActivity;

public class SecondActivity extends BaseActivity {
    public static final String EVENT_BOOK = "event_book";
    private TextView mTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_second;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTextView = (TextView) findViewById(R.id.tv);


        //btn onclick
        findViewById(R.id.bt_send_event_to_fir_act).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send event to first activity
                Apollo.get().send(MainActivity.EVENT_USER, new MainActivity.User("apollo"));
            }
        });
    }


    @Receive(tag = EVENT_BOOK, type = Receive.Type.STICKY)
    public void receiveBook(Book book) {
        mTextView.setText(book.toString());
        Toast.makeText(this, "SecondActivity receive book event" + book.toString(), Toast.LENGTH_SHORT).show();
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
