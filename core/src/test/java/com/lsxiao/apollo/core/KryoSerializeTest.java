package com.lsxiao.apollo.core;

import com.lsxiao.apollo.core.entity.Event;
import com.lsxiao.apollo.core.serialize.KryoSerializer;
import com.lsxiao.apollo.core.serialize.Serializable;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class KryoSerializeTest extends TestCase {
    private Serializable mKryo;
    private TestClass mTestClass;
    private Event mEvent;

    private static class TestClass {
        private String testString;
        private int testInt;
        private float testFloat;
        private double testDouble;
        private boolean testBool;

        public TestClass() {

        }

        public TestClass(String testString, int testInt, float testFloat, double testDouble, boolean testBool) {
            this.testString = testString;
            this.testInt = testInt;
            this.testFloat = testFloat;
            this.testDouble = testDouble;
            this.testBool = testBool;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestClass)) {
                return false;
            }
            TestClass toEqual = (TestClass) obj;
            return (toEqual.testBool == testBool) &&
                    (toEqual.testDouble == testDouble) &&
                    (toEqual.testFloat == testFloat) &&
                    (toEqual.testInt == testInt) &&
                    (toEqual.testString.equals(testString));
        }

        @Override
        public String toString() {
            return "TestClass{" +
                    "testString='" + testString + '\'' +
                    ", testInt=" + testInt +
                    ", testFloat=" + testFloat +
                    ", testDouble=" + testDouble +
                    ", testBool=" + testBool +
                    '}';
        }
    }

    @Before
    public void setUp() throws Exception {
        mTestClass = new TestClass("test", 1, 1f, 1d, true);
        mEvent = new Event("tag", mTestClass, 1, true);
        mKryo = new KryoSerializer();
    }

    @Test
    public void testTestClass() throws Exception {
        TestClass newTestClass = mKryo.deserialize(mKryo.serialize(mTestClass), TestClass.class);
        assertTrue(newTestClass.equals(mTestClass));
    }

    @Test
    public void testEvent() throws Exception {
        Event newEvent = mKryo.deserialize(mKryo.serialize(mEvent), Event.class);
        assertNotNull(newEvent);
        assertEquals(mEvent.getData(), newEvent.getData());
        assertEquals(mEvent.getPid(), newEvent.getPid());
        assertEquals(mEvent.getTag(), newEvent.getTag());
    }

}