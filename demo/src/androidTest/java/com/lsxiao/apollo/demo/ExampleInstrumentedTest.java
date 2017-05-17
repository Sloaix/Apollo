package com.lsxiao.apollo.demo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.apollo.core.Apollo;
import com.apollo.core.annotations.Receive;
import com.apollo.core.contract.ApolloBinder;
import com.lsxiao.apollo.generate.ApolloBinderGeneratorImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "EVENT_TAG";
    private String countStringEvent;

    private ApolloBinder binder = null;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Apollo.init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), appContext);

        binder = Apollo.bind(this);
    }

    @Test
    public void testBindAndPost() throws Exception {
        Apollo.emit("test", "test");
    }

    @Receive(TAG)
    public void test(String message) {
        System.out.println(message);
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }
}
