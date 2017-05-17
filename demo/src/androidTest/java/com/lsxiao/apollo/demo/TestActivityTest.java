package com.lsxiao.apollo.demo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.lsxiao.apollo.generate.ApolloBinderGeneratorImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-16 22:28
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class TestActivityTest {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule(TestActivity.class);


    private ApolloBinder binder = null;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Apollo.init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), appContext);

        binder = Apollo.bind(this);
    }


    @Test
    public void useAppContext() throws Exception {
        Apollo.emit("process", "test");
    }


    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }
}
