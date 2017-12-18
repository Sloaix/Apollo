package com.lsxiao.apollo.unittest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.annotations.ObserveOn;
import com.lsxiao.apollo.core.annotations.Receive;
import com.lsxiao.apollo.core.annotations.SubscribeOn;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.lsxiao.apollo.core.entity.SchedulerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BusTest {
    private static final String TAG = "EVENT_TAG";
    private ApolloBinder mApolloBinder;
    private Subscriber mSubscriber;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        Apollo.init(AndroidSchedulers.mainThread(), context);
        mSubscriber = Mockito.mock(Subscriber.class);
        mApolloBinder = Apollo.bind(mSubscriber);
    }


    @Test
    @UiThreadTest
    public void emitString() throws Exception {
        Apollo.emit(TAG, "hello");
        Mockito.verify(mSubscriber).onString("hello");
    }

    @Test
    @UiThreadTest
    public void emitInt() throws Exception {
        Apollo.emit(TAG, 1);
        Mockito.verify(mSubscriber).onInt(1);
    }


    @Test
    @UiThreadTest
    public void emitLong() throws Exception {
        Apollo.emit(TAG, 1L);
        Mockito.verify(mSubscriber).onLong(1L);
    }

    @Test
    @UiThreadTest
    public void emitBoolean() throws Exception {
        Apollo.emit(TAG, false, false);
        Mockito.verify(mSubscriber).onBoolean(false);
    }


    @Test
    @UiThreadTest
    public void emitChar() throws Exception {
        Apollo.emit(TAG, 'x');
        Mockito.verify(mSubscriber).onChar('x');
    }

    public class Subscriber {

        @Receive(TAG)
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void onString(String msg) {

        }

        @Receive(TAG)
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void onInt(int number) {

        }

        @Receive(TAG)
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void onLong(long number) {

        }

        @Receive(TAG)
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void onBoolean(boolean value) {

        }

        @Receive(TAG)
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void onChar(char value) {

        }
    }

    @After
    public void tearDown() throws Exception {
        mApolloBinder.unbind();
    }
}
