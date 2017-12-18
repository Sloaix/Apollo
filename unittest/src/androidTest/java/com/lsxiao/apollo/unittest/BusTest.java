package com.lsxiao.apollo.unittest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.annotations.ObserveOn;
import com.lsxiao.apollo.core.annotations.Receive;
import com.lsxiao.apollo.core.annotations.Sticky;
import com.lsxiao.apollo.core.annotations.SubscribeOn;
import com.lsxiao.apollo.core.annotations.Take;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.lsxiao.apollo.core.entity.SchedulerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;

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

        //for sticky test,before subscriber create and bind.
        Apollo.emit("receiveSticky", "apollo", true);

        mSubscriber = Mockito.mock(Subscriber.class);
        mApolloBinder = Apollo.bind(mSubscriber);
    }


    @Test
    @UiThreadTest
    public void emitString() throws Exception {
        Apollo.emit(TAG, "apollo");
        Mockito.verify(mSubscriber).onString("apollo");
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


    @Test
    @UiThreadTest
    public void receiveTimes() throws Exception {
        Apollo.emit("receiveTimes", "apollo");
        Mockito.verify(mSubscriber, times(1)).onTakeMultiTimes("apollo");

        Apollo.emit("receiveTimes", "apollo");
        Apollo.emit("receiveTimes", "apollo");
        Mockito.verify(mSubscriber, times(3)).onTakeMultiTimes("apollo");

        Apollo.emit("receiveTimes", "apollo");
        Apollo.emit("receiveTimes", "apollo");
        Mockito.verify(mSubscriber, atMost(4)).onTakeMultiTimes("apollo");
    }


    @Test
    @UiThreadTest
    public void receiveMultiTags() throws Exception {
        Apollo.emit("tag-1", "apollo");
        Mockito.verify(mSubscriber, times(1)).receiveMultiTags("apollo");
        Apollo.emit("tag-2", "apollo");
        Mockito.verify(mSubscriber, times(2)).receiveMultiTags("apollo");
    }


    @Test
    @UiThreadTest
    public void receiveNoParam() throws Exception {
        Apollo.emit("receiveNoParam");
        Mockito.verify(mSubscriber, times(1)).receiveNoParam();
    }

    @Test
    @UiThreadTest
    public void receiveSticky() throws Exception {
        Mockito.verify(mSubscriber, times(1)).receiveSticky("apollo");
    }


    public class Subscriber {
        @Receive("receiveSticky")
        @Sticky
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void receiveSticky(String msg) {

        }

        @Receive("receiveNoParam")
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void receiveNoParam() {

        }

        @Receive({"tag-1", "tag-2"})
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        public void receiveMultiTags(String msg) {

        }


        @Receive("receiveTimes")
        @SubscribeOn(SchedulerProvider.Tag.TRAMPOLINE)
        @ObserveOn(SchedulerProvider.Tag.TRAMPOLINE)
        @Take(4)
        public void onTakeMultiTimes(String msg) {

        }

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
