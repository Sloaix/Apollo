package com.apollo.core


import com.apollo.core.contract.ApolloBinder
import com.apollo.core.contract.ApolloBinderGenerator
import com.apollo.core.entity.Event
import com.apollo.core.entity.SchedulerProvider
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

/**
 * author lsxiao
 * date 2016-05-09 17:27
 */
class Apollo private constructor() {
    private val mFlowableProcessor: FlowableProcessor<Event> by lazy {
        PublishProcessor.create<Event>().toSerialized()
    }
    //用于保存stick事件
    private val mStickyEventMap: MutableMap<String, Event> = HashMap()
    //用于保存SubscriptionBinder
    private val mBindTargetMap: MutableMap<Int, ApolloBinder> = HashMap()
    private var mApolloBinderGenerator: ApolloBinderGenerator? = null
    var schedulerProvider: SchedulerProvider? = null

    fun init(main: Scheduler?, binder: ApolloBinderGenerator?) {
        if (null == binder) {
            throw NullPointerException("the binder must be not null")
        }

        if (null == main) {
            throw NullPointerException("the main scheduler must be not null")
        }

        if (null == mApolloBinderGenerator) {
            mApolloBinderGenerator = binder
        }

        if (null == schedulerProvider) {
            schedulerProvider = SchedulerProvider.create(main)
        }
    }

    /**
     * 判断是否有订阅者
     */
    fun hasSubscribers(): Boolean {
        return mFlowableProcessor.hasSubscribers()
    }

    /**
     * 根据tag和eventType获取指定类型的Sticky事件
     */
    fun <T> getStickyEvent(tag: String, eventType: Class<T>): T? {
        synchronized(mStickyEventMap) {
            val o = mStickyEventMap[tag]?.data as Any
            if (o.javaClass.canonicalName == eventType.canonicalName) {
                return eventType.cast(o)
            }
        }
        return null
    }

    /**
     * 根据tag获取Sticky事件
     */
    fun getStickyEvent(tag: String): Any? {
        synchronized(mStickyEventMap) {
            return if (mStickyEventMap[tag] == null) null else mStickyEventMap[tag]?.data
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    fun removeStickyEvent(tag: String) {
        synchronized(mStickyEventMap) {
            mStickyEventMap.remove(tag)
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    fun removeStickyEvent(tags: Array<String>) {
        for (tag in tags) {
            removeStickyEvent(tag)
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    fun removeAllStickyEvents() {
        synchronized(mStickyEventMap) {
            mStickyEventMap.clear()
        }
    }


    /**
     * 发送event

     * @param event Event
     */
    private fun send(event: Event) {
        mFlowableProcessor.onNext(event)
    }

    /**
     * 发送event

     * @param tag    Tag
     * *
     * @param actual 内容
     */
    fun send(tag: String, actual: Any = Any()) {
        val event = Event(tag, actual)
        mFlowableProcessor.onNext(event)
    }

    /**
     * 发送一个新Sticky事件
     */
    fun sendSticky(tag: String, actual: Any) {
        synchronized(mStickyEventMap) {
            val event = Event(tag, actual, true)
            mStickyEventMap.put(tag, event)
            send(event)
        }
    }

    fun sendSticky(tag: String) {
        synchronized(mStickyEventMap) {
            val event = Event(tag, Any(), true)
            mStickyEventMap.put(tag, event)
            mFlowableProcessor.onNext(event)
        }
    }

    /**
     * 绑定Activity或者Fragment

     * @param o Object
     * *
     * @return ApolloBinder
     */
    fun bind(o: Any?): ApolloBinder {
        if (null == o) {
            throw NullPointerException("object to subscribe must not be null")
        }

        return uniqueBind(o)
    }

    /**
     * 唯一绑定,避免重复绑定到相同的对象

     * @param o Object
     * *
     * @return ApolloBinder
     */
    private fun uniqueBind(o: Any): ApolloBinder {
        val uniqueId = System.identityHashCode(o)

        var binder: ApolloBinder

        //对象已有绑定记录
        if (mBindTargetMap.containsKey(uniqueId)) {
            binder = mBindTargetMap[uniqueId] as ApolloBinder
            //绑定已经解绑
            if (binder.isUnbind()) {
                //移除已经解绑的binder
                mBindTargetMap.remove(uniqueId)
                //重新绑定
                binder = mApolloBinderGenerator!!.generate(o)
                //保存到map中
                mBindTargetMap.put(uniqueId, binder)
            }
        } else {
            binder = mApolloBinderGenerator!!.generate(o)
            mBindTargetMap.put(uniqueId, binder)
        }
        return binder
    }


    fun toFlowable(tag: String): Flowable<Any> {
        return toFlowable(arrayOf(tag), Any::class.java)
    }

    fun toFlowable(tags: Array<String>): Flowable<Any> {
        return toFlowable(tags, Any::class.java)
    }

    fun <T> toFlowable(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
        if (null == eventType) {
            throw NullPointerException("the eventType must be not null")
        }

        if (null == tags) {
            throw NullPointerException("the tags must be not null")
        }

        if (tags.isEmpty()) {
            throw IllegalArgumentException("the tags must be not empty")
        }

        return mFlowableProcessor
                .filter { event ->
                    Arrays.asList(*tags).contains(event.tag) && eventType.isInstance(event.data)
                }
                .flatMap { event -> Flowable.just(eventType.cast(event.data)) }
    }

    fun toFlowableSticky(tag: String): Flowable<Any> {
        return toFlowableSticky(arrayOf(tag))
    }

    fun toFlowableSticky(tags: Array<String>): Flowable<Any> {
        return toFlowableSticky(tags, Any::class.java)
    }

    fun <T> toFlowableSticky(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
        if (null == eventType) {
            throw NullPointerException("the eventType must be not null")
        }

        if (null == tags) {
            throw NullPointerException("the tags must be not null")
        }

        if (tags.isEmpty()) {
            throw IllegalArgumentException("the tags must be not empty")
        }

        synchronized(mStickyEventMap) {
            //普通事件的被观察者
            val flowable = toFlowable(tags, eventType)

            val stickyEvents = ArrayList<Event>()
            for (tag in tags) {
                //sticky事件
                val event = mStickyEventMap[tag]
                if (event != null) {
                    mStickyEventMap[tag]?.let { stickyEvents.add(it) }
                }
            }

            if (!stickyEvents.isEmpty()) {
                //合并事件序列
                return Flowable.fromIterable(stickyEvents)
                        .flatMap { event -> Flowable.just(eventType.cast(event.data)) }.mergeWith(flowable)

            } else {
                return flowable
            }
        }
    }

    companion object {
        private var sInstance: Apollo? = null

        /**
         * 返回一个Apollo的单例对象

         * @return Apollo
         */
        @JvmStatic @Synchronized fun get(): Apollo {
            if (null == sInstance) {
                sInstance = Apollo()
            }
            return sInstance as Apollo
        }
    }

}