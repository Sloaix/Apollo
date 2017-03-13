package com.lsxiao.apllo;


import com.lsxiao.apllo.contract.ApolloBinder;
import com.lsxiao.apllo.contract.ApolloBinderGenerator;
import com.lsxiao.apllo.entity.Event;
import com.lsxiao.apllo.entity.SchedulerProvider;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;


/**
 * author lsxiao
 * date 2016-05-09 17:27
 */
public class Apollo {
    private FlowableProcessor<Event> mFlowableProcessor;
    private final Map<String, Event> mStickyEventMap;//用于保存stick事件
    private final Map<Integer, ApolloBinder> mBindTargetMap;//用于保存SubscriptionBinder
    private ApolloBinderGenerator mApolloBinderGenerator;
    private SchedulerProvider mSchedulerProvider;
    private static Apollo sInstance;

    private Apollo() {
        //PublishProcessor 会发送订阅者从订阅之后的事件序列,这意味着没订阅前的事件序列不会被发送到当前订阅者
        mFlowableProcessor = PublishProcessor.create();

        //SerializedProcessor是线程安全的
        mFlowableProcessor = mFlowableProcessor.toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
        mBindTargetMap = new HashMap<>();
    }


    public void init(ApolloBinderGenerator binder, SchedulerProvider schedulerProvider) {
        if (null == binder) {
            throw new NullPointerException("the binder must be not null");
        }

        if (null == schedulerProvider) {
            throw new NullPointerException("the schedulerProvider must be not null");
        }

        if (null == mApolloBinderGenerator) {
            mApolloBinderGenerator = binder;
        }

        if (null == mSchedulerProvider) {
            mSchedulerProvider = schedulerProvider;
        }
    }

    /**
     * 返回一个Apollo的单例对象
     *
     * @return Apollo
     */
    public synchronized static Apollo get() {
        if (null == sInstance) {
            sInstance = new Apollo();
        }
        return sInstance;
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasSubscribers() {
        return mFlowableProcessor.hasSubscribers();
    }

    /**
     * 根据tag和eventType获取指定类型的Sticky事件
     */
    public <T> T getStickyEvent(String tag, Class<T> eventType) {
        synchronized (mStickyEventMap) {
            Object o = mStickyEventMap.get(tag).getData();
            if (o.getClass().getCanonicalName().equals(eventType.getCanonicalName())) {
                return eventType.cast(o);
            }
        }
        return null;
    }

    /**
     * 根据tag获取Sticky事件
     */
    public Object getStickyEvent(String tag) {
        synchronized (mStickyEventMap) {
            return mStickyEventMap.get(tag) == null ? null : mStickyEventMap.get(tag).getData();
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public void removeStickyEvent(String tag) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.remove(tag);
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public void removeStickyEvent(String[] tags) {
        for (String tag : tags) {
            removeStickyEvent(tag);
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }


    /**
     * 发送event
     *
     * @param event Event
     */
    private void send(Event event) {
        mFlowableProcessor.onNext(event);
    }

    /**
     * 发送event
     *
     * @param tag    Tag
     * @param actual 内容
     */
    public void send(String tag, Object actual) {
        Event event = new Event(tag, actual);
        send(event);
    }

    public void send(String tag) {
        send(tag, new Object());
    }

    /**
     * 发送一个新Sticky事件
     */
    public void sendSticky(String tag, Object actual) {
        synchronized (mStickyEventMap) {
            Event event = new Event(tag, actual, true);
            mStickyEventMap.put(tag, event);
            send(event);
        }
    }

    public void sendSticky(String tag) {
        synchronized (mStickyEventMap) {
            Event event = new Event(tag, new Object(), true);
            mStickyEventMap.put(tag, event);
            mFlowableProcessor.onNext(event);
        }
    }

    /**
     * 绑定Activity或者Fragment
     *
     * @param o Object
     * @return ApolloBinder
     */
    public ApolloBinder bind(Object o) {
        if (null == o) {
            throw new NullPointerException("object to subscribe must not be null");
        }

        return uniqueBind(o);
    }

    /**
     * 唯一绑定,避免重复绑定到相同的对象
     *
     * @param o Object
     * @return ApolloBinder
     */
    private ApolloBinder uniqueBind(Object o) {
        final int uniqueId = System.identityHashCode(o);

        ApolloBinder binder;

        //对象已有绑定记录
        if (mBindTargetMap.containsKey(uniqueId)) {
            binder = mBindTargetMap.get(uniqueId);
            //绑定已经解绑
            if (binder.isUnbind()) {
                //移除已经解绑的binder
                mBindTargetMap.remove(uniqueId);
                //重新绑定
                binder = mApolloBinderGenerator.generate(o);
                //保存到map中
                mBindTargetMap.put(uniqueId, binder);
            }
        } else {
            binder = mApolloBinderGenerator.generate(o);
            mBindTargetMap.put(uniqueId, binder);
        }
        return binder;
    }


    public Flowable<Object> toFlowable(final String tag) {
        return toFlowable(new String[]{tag}, Object.class);
    }

    public Flowable<Object> toFlowable(final String[] tags) {
        return toFlowable(tags, Object.class);
    }

    public <T> Flowable<T> toFlowable(final String[] tags, final Class<T> eventType) {
        if (null == eventType) {
            throw new NullPointerException("the eventType must be not null");
        }

        if (null == tags) {
            throw new NullPointerException("the tags must be not null");
        }

        if (0 == tags.length) {
            throw new IllegalArgumentException("the tags must be not empty");
        }

        return mFlowableProcessor
                .filter(new Predicate<Event>() {
                    @Override
                    public boolean test(Event event) throws Exception {
                        return Arrays.asList(tags).contains(event.getTag()) &&
                                //如果subscriberEvent.getData() = null,不用再去检查是不是特定类型或者其子类的实例
                                (event.getData() == null || eventType.isInstance(event.getData()));
                    }
                })
                .flatMap(new Function<Event, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(Event event) throws Exception {
                        return Flowable.just(eventType.cast(event.getData()));
                    }
                });
    }

    public Flowable<Object> toFlowableSticky(final String tag) {
        return toFlowableSticky(new String[]{tag});
    }

    public Flowable<Object> toFlowableSticky(final String[] tags) {
        return toFlowableSticky(tags, Object.class);
    }

    public <T> Flowable<T> toFlowableSticky(final String[] tags, final Class<T> eventType) {
        if (null == eventType) {
            throw new NullPointerException("the eventType must be not null");
        }

        if (null == tags) {
            throw new NullPointerException("the tags must be not null");
        }

        if (0 == tags.length) {
            throw new IllegalArgumentException("the tags must be not empty");
        }

        synchronized (mStickyEventMap) {
            //普通事件的被观察者
            Flowable<T> flowable = toFlowable(tags, eventType);

            final List<Event> stickyEvents = new ArrayList<>();
            for (String tag : tags) {
                //sticky事件
                final Event event = mStickyEventMap.get(tag);
                if (event != null) {
                    stickyEvents.add(mStickyEventMap.get(tag));
                }
            }

            if (!stickyEvents.isEmpty()) {
                //合并事件序列
                return Flowable.fromIterable(stickyEvents)
                        .flatMap(new Function<Event, Publisher<T>>() {
                            @Override
                            public Publisher<T> apply(Event event) throws Exception {
                                return Flowable.just(eventType.cast(event.getData()));
                            }
                        }).mergeWith(flowable);

            } else {
                return flowable;
            }
        }
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

}