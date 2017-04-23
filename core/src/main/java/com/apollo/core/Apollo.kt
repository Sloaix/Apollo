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
import kotlin.properties.Delegates

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
    private var mApolloBinderGenerator: ApolloBinderGenerator by Delegates.notNull()
    private var mSchedulerProvider: SchedulerProvider by Delegates.notNull()

    companion object {
        private var sInstance: Apollo? = null

        /**
         * 返回一个Apollo的单例对象

         * @return Apollo
         */
        private @JvmStatic @Synchronized fun get(): Apollo {
            if (null == sInstance) {
                sInstance = Apollo()
            }
            return sInstance as Apollo
        }

        @JvmStatic fun init(main: Scheduler, binder: ApolloBinderGenerator) {
            get().mApolloBinderGenerator = binder
            get().mSchedulerProvider = SchedulerProvider.create(main)
        }


        /**
         * 判断是否有订阅者
         */
        @JvmStatic fun hasSubscribers(): Boolean {
            return get().mFlowableProcessor.hasSubscribers()
        }

        /**
         * 绑定Activity或者Fragment

         * @param o Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic fun bind(o: Any?): ApolloBinder {
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
        @JvmStatic private fun uniqueBind(o: Any): ApolloBinder {
            val uniqueId = System.identityHashCode(o)

            var binder: ApolloBinder

            //对象已有绑定记录
            if (get().mBindTargetMap.containsKey(uniqueId)) {
                binder = get().mBindTargetMap[uniqueId] as ApolloBinder
                //绑定已经解绑
                if (binder.isUnbind()) {
                    //移除已经解绑的binder
                    get().mBindTargetMap.remove(uniqueId)
                    //重新绑定
                    binder = get().mApolloBinderGenerator!!.generate(o)
                    //保存到map中
                    get().mBindTargetMap.put(uniqueId, binder)
                }
            } else {
                binder = get().mApolloBinderGenerator!!.generate(o)
                get().mBindTargetMap.put(uniqueId, binder)
            }
            return binder
        }


        @JvmStatic fun toFlowable(tag: String): Flowable<Any> {
            return toFlowable(arrayOf(tag), Any::class.java)
        }

        @JvmStatic fun toFlowable(tags: Array<String>): Flowable<Any> {
            return toFlowable(tags, Any::class.java)
        }

        @JvmStatic fun <T> toFlowable(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
            if (null == eventType) {
                throw NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw IllegalArgumentException("the tags must be not empty")
            }

            return get().mFlowableProcessor
                    .filter { event ->
                        Arrays.asList(*tags).contains(event.tag) && eventType.isInstance(event.data)
                    }
                    .flatMap { event -> Flowable.just(eventType.cast(event.data)) }
        }

        @JvmStatic fun toFlowableSticky(tag: String): Flowable<Any> {
            return toFlowableSticky(arrayOf(tag))
        }

        @JvmStatic fun toFlowableSticky(tags: Array<String>): Flowable<Any> {
            return toFlowableSticky(tags, Any::class.java)
        }

        @JvmStatic fun <T> toFlowableSticky(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
            if (null == eventType) {
                throw NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw IllegalArgumentException("the tags must be not empty")
            }

            synchronized(get().mStickyEventMap) {
                //普通事件的被观察者
                val flowable = toFlowable(tags, eventType)

                val stickyEvents = ArrayList<Event>()
                for (tag in tags) {
                    //sticky事件
                    val event = get().mStickyEventMap[tag]
                    if (event != null) {
                        get().mStickyEventMap[tag]?.let { stickyEvents.add(it) }
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

        @JvmStatic fun getSchedulerProvider(): SchedulerProvider = get().mSchedulerProvider

        @JvmStatic fun emit(tag: String) = synchronized(get().mStickyEventMap) {
            emit(tag, Any(), false)
        }

        @JvmStatic fun emit(tag: String, actual: Any = Any()) = synchronized(get().mStickyEventMap) {
            emit(tag, actual, false)
        }

        @JvmStatic fun emit(tag: String, sticky: Boolean = false) = synchronized(get().mStickyEventMap) {
            emit(tag, Any(), sticky)
        }

        @JvmStatic fun emit(tag: String, actual: Any = Any(), sticky: Boolean = false) = synchronized(get().mStickyEventMap) {
            val event = Event(tag, actual, sticky)
            if (sticky) {
                get().mStickyEventMap.put(tag, event)
            }
            get().mFlowableProcessor.onNext(event)
        }


        @JvmStatic fun removeStickyEvent(vararg tags: String) = tags.forEach { tag ->
            synchronized(get().mStickyEventMap) {
                get().mStickyEventMap.remove(tag)
            }
        }

        @JvmStatic fun removeAllStickyEvent() = {
            synchronized(get().mStickyEventMap) {
                get().mStickyEventMap.clear()
            }
        }


        /**
         * 根据tag和eventType获取指定类型的Sticky事件
         */
        @JvmStatic fun <T> getStickyEvent(tag: String, eventType: Class<T>): T? {
            synchronized(get().mStickyEventMap) {
                val o = get().mStickyEventMap[tag]?.data as Any
                if (o.javaClass.canonicalName == eventType.canonicalName) {
                    return eventType.cast(o)
                }
            }
            return null
        }

        /**
         * 根据tag获取Sticky事件
         */
        @JvmStatic fun getStickyEvent(tag: String): Any? {
            synchronized(get().mStickyEventMap) {
                return if (get().mStickyEventMap[tag] == null) null else get().mStickyEventMap[tag]?.data
            }
        }
    }

}