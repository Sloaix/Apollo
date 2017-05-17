package com.lsxiao.apollo.demo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.annotations.Receive;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.lsxiao.apollo.core.entity.SchedulerProvider;
import com.lsxiao.apollo.core.serialize.KryoSerializer;
import com.lsxiao.apollo.generate.ApolloBinderGeneratorImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "EVENT_TAG";
    private String countStringEvent;
    private Context mContext;
    private ApolloBinder binder = null;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();

        Apollo.init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), mContext);

    }

    @Test
    public void testContext() {
        assertEquals(Apollo.getContext(), mContext);
    }

    @Test
    public void testSchedulerProvider() {
        assertEquals(Apollo.getSchedulerProvider().get(SchedulerProvider.Tag.MAIN), AndroidSchedulers.mainThread());
        assertEquals(Apollo.getSchedulerProvider().get(SchedulerProvider.Tag.IO), Schedulers.io());
        assertEquals(Apollo.getSchedulerProvider().get(SchedulerProvider.Tag.COMPUTATION), Schedulers.computation());
        assertEquals(Apollo.getSchedulerProvider().get(SchedulerProvider.Tag.SINGLE), Schedulers.single());
        assertEquals(Apollo.getSchedulerProvider().get(SchedulerProvider.Tag.TRAMPOLINE), Schedulers.trampoline());
    }

    @Test
    public void testSticky() {
        String msg = "msg";
        String tag = "tag";
        Apollo.emit(tag, msg, true);
        assertEquals(Apollo.getStickyEvent(tag), msg);

        Apollo.removeStickyEvent(tag);
        assertNull(Apollo.getStickyEvent(tag));

        Apollo.emit(tag, msg, true);
        assertEquals(Apollo.getStickyEvent(tag), msg);

        Apollo.removeAllStickyEvent();
        assertNull(Apollo.getStickyEvent(tag));
    }

    @Test
    public void testBindUnBind() throws Exception {
        ApolloBinder binder = Apollo.bind(this);
        assertTrue(Apollo.isBind(this));
        binder.unbind();
        assertFalse(Apollo.isBind(this));
    }

    @Test
    public void testSerializer() {
        assertNotNull(Apollo.getSerializer());
        assertTrue(Apollo.getSerializer() instanceof KryoSerializer);
    }

    @Receive(TAG)
    public void test(String message) {
        System.out.println(message);
    }

    @After
    public void tearDown() throws Exception {
    }
}
