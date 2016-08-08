package com.lsxiao.apollo.demo.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apollo.demo.R;
import com.lsxiao.apollo.demo.base.BaseActivity;

public class BookActivity extends BaseActivity {
    private TextView mTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTextView = (TextView) findViewById(R.id.tv);
    }


    @Receive(tag = MainActivity.EVENT_SHOW_BOOK, type = Receive.Type.STICKY)
    public void receiveBook(MainActivity.Book book) {
        Log.d("apollo", "BookActivity receive book event" + book.toString());
        mTextView.setText(book.toString());
    }
}
