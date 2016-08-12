package com.lsxiao.apllo;


import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apllo.entity.SubscriberEvent;
import com.lsxiao.apllo.entity.SubscriptionBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * author lsxiao
 * date 2016-05-09 17:27
 */
public class Apollo {
    private SerializedSubject<SubscriberEvent, SubscriberEvent> mPublishSubject;
    private final Map<String, SubscriberEvent> mStickyEventMap;//用于保存stick事件
    private Map<Integer, SubscriptionBinder> mBindTargetMap;//用于保存SubscriptionBinder
    private static Apollo sInstance;
    private SubscriberBinder mSubscriberBinder;
    private Thread mThread;

    private Apollo() {
        //SerializedSubject是线程安全的
        //PublishSubject 会发送订阅者从订阅之后的事件序列,这意味着没订阅前的事件序列不会被发送到当前订阅者
        mPublishSubject = new SerializedSubject<>(PublishSubject.<SubscriberEvent>create());
        mStickyEventMap = new ConcurrentHashMap<>();
        mBindTargetMap = new HashMap<>();
    }


    public void init(SubscriberBinder binder, Scheduler main) {
        if (null == binder) {
            throw new NullPointerException("the binder must be not null");
        }

        if (null == main) {
            throw new NullPointerException("the main scheduler must be not null");
        }

        if (null == mSubscriberBinder) {
            mSubscriberBinder = binder;
        }

        if (null == mThread) {
            mThread = new Thread(main);
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
    public boolean hasObservers() {
        return mPublishSubject.hasObservers();
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
     * @param tag Tag
     * @param o   内容
     */
    public void send(String tag, Object o) {
        SubscriberEvent event = new SubscriberEvent(tag, o);
        mPublishSubject.onNext(event);
    }

    public void send(String tag) {
        SubscriberEvent event = new SubscriberEvent(tag, new Object());
        mPublishSubject.onNext(event);
    }

    /**
     * 发送一个新Sticky事件
     */
    public void sendSticky(String tag, Object o) {
        synchronized (mStickyEventMap) {
            SubscriberEvent event = new SubscriberEvent(tag, o, true);
            mStickyEventMap.put(tag, event);
            mPublishSubject.onNext(event);
        }
    }

    public void sendSticky(String tag) {
        synchronized (mStickyEventMap) {
            SubscriberEvent event = new SubscriberEvent(tag, new Object(), true);
            mStickyEventMap.put(tag, event);
            mPublishSubject.onNext(event);
        }
    }

    /**
     * 绑定Activity或者Fragment
     *
     * @param o Object
     * @return SubscriptionBinder
     */
    public SubscriptionBinder bind(Object o) {
        if (null == o) {
            throw new NullPointerException("object to bind must not be null");
        }

        return uniqueBind(o);
    }

    /**
     * 唯一绑定,避免重复绑定到相同的对象
     *
     * @param o Object
     * @return SubscriptionBinder
     */
    private SubscriptionBinder uniqueBind(Object o) {
        int uniqueId = System.identityHashCode(o);

        SubscriptionBinder binder;

        //对象已有绑定记录
        if (mBindTargetMap.containsKey(uniqueId)) {
            binder = mBindTargetMap.get(uniqueId);
            //绑定已经解绑
            if (binder.isUnbind()) {
                //移除已经解绑的binder
                mBindTargetMap.remove(uniqueId);
                //重新绑定
                binder = mSubscriberBinder.bind(o);
                //保存到map中
                mBindTargetMap.put(uniqueId, binder);
            }
        } else {
            binder = mSubscriberBinder.bind(o);
            mBindTargetMap.put(uniqueId, binder);
        }
        return binder;
    }


    public Observable<Object> toObservable(final String tag) {
        return toObservable(new String[]{tag}, Object.class);
    }

    public Observable<Object> toObservable(final String[] tags) {
        return toObservable(tags, Object.class);
    }

    /**
     * 返回普通事件类型的被观察者
     *
     * @param eventType 只接受eventType类型的响应,ofType = filter + cast
     * @return Observable
     */
    public <T> Observable<T> toObservable(final String tag, final Class<T> eventType) {
        return toObservable(new String[]{tag}, eventType);
    }

    public <T> Observable<T> toObservable(final String[] tags, final Class<T> eventType) {
        if (null == eventType) {
            throw new NullPointerException("the eventType must be not null");
        }

        if (null == tags) {
            throw new NullPointerException("the tags must be not null");
        }

        if (0 == tags.length) {
            throw new IllegalArgumentException("the tags must be not empty");
        }

        return mPublishSubject
                .filter(new Func1<SubscriberEvent, Boolean>() {
                    @Override
                    public Boolean call(SubscriberEvent subscriberEvent) {
                        return Arrays.asList(tags).contains(subscriberEvent.getTag()) &&
                                //如果subscriberEvent.getData() = null,不用再去检查是不是特定类型或者其子类的实例
                                (subscriberEvent.getData() == null || eventType.isInstance(subscriberEvent.getData()));
                    }
                })
                .flatMap(new Func1<SubscriberEvent, Observable<T>>() {
                    @Override
                    public Observable<T> call(SubscriberEvent subscriberEvent) {
                        return Observable.just(eventType.cast(subscriberEvent.getData()));
                    }
                });
    }


    public Observable toObservableSticky(final String tag) {
        return toObservable(new String[]{tag});
    }

    public Observable<Object> toObservableSticky(final String[] tags) {
        return toObservableSticky(tags, Object.class);
    }


    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservableSticky(String tag, final Class<T> eventType) {
        return toObservableSticky(new String[]{tag}, eventType);
    }

    public <T> Observable<T> toObservableSticky(final String[] tags, final Class<T> eventType) {
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
            Observable<T> observable = toObservable(tags, eventType);

            final List<SubscriberEvent> stickyEventList = new ArrayList<>();
            for (String tag : tags) {
                //sticky事件
                final SubscriberEvent event = mStickyEventMap.get(tag);
                if (event != null) {
                    stickyEventList.add(mStickyEventMap.get(tag));
                }
            }

            if (!stickyEventList.isEmpty()) {
                //合并事件序列
                return Observable.from(stickyEventList)
                        .flatMap(new Func1<SubscriberEvent, Observable<T>>() {
                            @Override
                            public Observable<T> call(SubscriberEvent subscriberEvent) {
                                return Observable.just(eventType.cast(subscriberEvent.getData()));
                            }
                        }).mergeWith(observable);

            } else {
                return observable;
            }
        }
    }

    public interface SubscriberBinder {
        SubscriptionBinder bind(Object object);
    }

    public Thread getThread() {
        return mThread;
    }

    public class Thread {
        private Scheduler mMain;//need init
        private Scheduler mIO = Schedulers.io();
        private Scheduler mComputation = Schedulers.computation();
        private Scheduler mTrampoline = Schedulers.trampoline();
        private Scheduler mImmediate = Schedulers.immediate();
        private Scheduler mNew = Schedulers.newThread();

        public Thread(Scheduler mainScheduler) {
            mMain = mainScheduler;
        }

        public Scheduler getMain() {
            return mMain;
        }

        public Scheduler getIO() {
            return mIO;
        }

        public Scheduler getComputation() {
            return mComputation;
        }

        public Scheduler getTrampoline() {
            return mTrampoline;
        }

        public Scheduler getImmediate() {
            return mImmediate;
        }

        public Scheduler getNew() {
            return mNew;
        }

        public Scheduler get(Receive.Thread thread) {
            switch (thread) {
                case MAIN: {
                    return getMain();
                }
                case IO: {
                    return getIO();
                }
                case COMPUTATION: {
                    return getComputation();
                }
                case TRAMPOLINE: {
                    return getTrampoline();
                }
                case IMMEDIATE: {
                    return getImmediate();
                }
                case NEW: {
                    return getNew();
                }
                default: {
                    return getMain();
                }
            }
        }
    }

}