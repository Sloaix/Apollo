package com.lsxiao.apollo.core

import com.lsxiao.apollo.core.contract.ApolloBinder
import com.lsxiao.apollo.core.contract.ApolloBinderGenerator
import com.lsxiao.apollo.core.entity.Event
import com.lsxiao.apollo.core.entity.SchedulerProvider
import com.lsxiao.apollo.core.serialize.KryoSerializer
import com.lsxiao.apollo.core.serialize.Serializable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor


/**
 * author lsxiao
 * date 2016-05-09 17:27
 */
class Apollo private constructor() {
    private val mFlowableProcessor: FlowableProcessor<Event> by lazy {
        PublishProcessor.create<Event>().toSerialized()
    }
    //用于保存stick事件
    private val mStickyEventMap: MutableMap<String, Event> = java.util.HashMap()
    //用于保存SubscriptionBinder
    private val mBindTargetMap: MutableMap<Int, ApolloBinder> = java.util.HashMap()
    private var mApolloBinderGenerator: ApolloBinderGenerator by kotlin.properties.Delegates.notNull()
    private var mSchedulerProvider: SchedulerProvider by kotlin.properties.Delegates.notNull()
    private var mContext: Any by kotlin.properties.Delegates.notNull()
    private var mSerializer: Serializable = KryoSerializer()
    private var mIPCEnable = false

    companion object {
        private var sInstance: Apollo? = null

        /**
         * 返回一个Apollo的单例对象

         * @return Apollo
         */
        @JvmStatic
        @Synchronized
        private fun get(): Apollo {
            if (null == Apollo.Companion.sInstance) {
                Apollo.Companion.sInstance = Apollo()
            }
            return Apollo.Companion.sInstance as Apollo
        }


        @JvmStatic
        fun init(main: Scheduler, binder: ApolloBinderGenerator, context: Any) {
            Apollo.get().mApolloBinderGenerator = binder
            Apollo.get().mSchedulerProvider = SchedulerProvider.Companion.create(main)
            Apollo.get().mContext = context
            Apollo.get().mApolloBinderGenerator.registerReceiver()
        }

        @JvmStatic
        fun init(main: Scheduler, binder: ApolloBinderGenerator, context: Any, ipcEnable: Boolean = false) {
            Apollo.get().mApolloBinderGenerator = binder
            Apollo.get().mSchedulerProvider = SchedulerProvider.Companion.create(main)
            Apollo.get().mContext = context
            Apollo.get().mIPCEnable = ipcEnable
            if (ipcEnable) {
                Apollo.get().mApolloBinderGenerator.registerReceiver()
            }
        }


        @JvmStatic
        fun serializer(serializer: Serializable) {
            Apollo.get().mSerializer = serializer
        }

        @JvmStatic
        fun getSerializer() = Apollo.get().mSerializer

        @Deprecated(message = "this method is not support ipc", replaceWith = ReplaceWith("use init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), getApplicationContext()) to instead"), level = DeprecationLevel.WARNING)
        @JvmStatic
        fun init(main: Scheduler, binder: ApolloBinderGenerator) {
            Apollo.Companion.init(main, binder, Any())
        }

        /**
         * 判断是否有订阅者
         */
        @JvmStatic
        fun hasSubscribers(): Boolean {
            return Apollo.get().mFlowableProcessor.hasSubscribers()
        }

        /**
         * 绑定Activity或者Fragment

         * @param o Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic
        fun bind(o: Any?): ApolloBinder {
            if (null == o) {
                throw java.lang.NullPointerException("object to subscribe must not be null")
            }

            return Apollo.Companion.uniqueBind(o)
        }


        /**
         * 绑定Activity或者Fragment

         * @param o Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic
        fun isBind(o: Any?): Boolean {
            if (o == null) {
                return false
            }
            val uniqueId = System.identityHashCode(o)
            return Apollo.get().mBindTargetMap.containsKey(uniqueId)
        }

        /**
         * 唯一绑定,避免重复绑定到相同的对象

         * @param o Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic
        private fun uniqueBind(o: Any): ApolloBinder {
            val uniqueId = System.identityHashCode(o)

            var binder: ApolloBinder

            //对象已有绑定记录
            if (Apollo.get().mBindTargetMap.containsKey(uniqueId)) {
                binder = Apollo.get().mBindTargetMap[uniqueId] as ApolloBinder
                //绑定已经解绑
                if (binder.isUnbind()) {
                    //移除已经解绑的binder
                    Apollo.get().mBindTargetMap.remove(uniqueId)
                    //重新绑定
                    binder = Apollo.get().mApolloBinderGenerator.generate(o)
                    //保存到map中
                    Apollo.get().mBindTargetMap.put(uniqueId, binder)
                }
            } else {
                binder = Apollo.get().mApolloBinderGenerator.generate(o)
                Apollo.get().mBindTargetMap.put(uniqueId, binder)
            }
            return binder
        }


        @JvmStatic
        fun toFlowable(tag: String): Flowable<Any> {
            return Apollo.Companion.toFlowable(arrayOf(tag), Any::class.java)
        }

        @JvmStatic
        fun toFlowable(tags: Array<String>): Flowable<Any> {
            return Apollo.Companion.toFlowable(tags, Any::class.java)
        }

        @JvmStatic
        fun <T> toFlowable(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
            if (null == eventType) {
                throw java.lang.NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw java.lang.NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw java.lang.IllegalArgumentException("the tags must be not empty")
            }

            return Apollo.get().mFlowableProcessor
                    .filter { event ->
                        java.util.Arrays.asList(*tags).contains(event.tag) && eventType.isInstance(event.data)
                    }
                    .flatMap { event -> Flowable.just(eventType.cast(event.data)) }
        }

        @JvmStatic
        fun toFlowableSticky(tag: String): Flowable<Any> {
            return Apollo.Companion.toFlowableSticky(arrayOf(tag))
        }

        @JvmStatic
        fun toFlowableSticky(tags: Array<String>): Flowable<Any> {
            return Apollo.Companion.toFlowableSticky(tags, Any::class.java)
        }

        @JvmStatic
        fun <T> toFlowableSticky(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
            if (null == eventType) {
                throw java.lang.NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw java.lang.NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw java.lang.IllegalArgumentException("the tags must be not empty")
            }

            synchronized(Apollo.get().mStickyEventMap) {
                //普通事件的被观察者
                val flowable = Apollo.Companion.toFlowable(tags, eventType)

                val stickyEvents = java.util.ArrayList<Event>()
                for (tag in tags) {
                    //sticky事件
                    val event = Apollo.get().mStickyEventMap[tag]
                    if (event != null) {
                        Apollo.get().mStickyEventMap[tag]?.let { stickyEvents.add(it) }
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

        @JvmStatic
        fun getSchedulerProvider(): SchedulerProvider = Apollo.get().mSchedulerProvider

        @JvmStatic
        fun getContext(): Any = Apollo.get().mContext


        @JvmStatic
        fun transfer(event: Event) = synchronized(Apollo.get().mStickyEventMap) {
            if (Apollo.get().mIPCEnable) {
                if (event.isSticky) {
                    Apollo.get().mStickyEventMap.put(event.tag, event)
                }
                Apollo.get().mFlowableProcessor.onNext(event)
            }
        }

        @JvmStatic
        fun emit(tag: String) = synchronized(Apollo.get().mStickyEventMap) {
            Apollo.Companion.emit(tag, Any(), false)
        }

        @JvmStatic
        fun emit(tag: String, actual: Any = Any()) = synchronized(Apollo.get().mStickyEventMap) {
            Apollo.Companion.emit(tag, actual, false)
        }

        @JvmStatic
        fun emit(tag: String, sticky: Boolean = false) = synchronized(Apollo.get().mStickyEventMap) {
            Apollo.Companion.emit(tag, Any(), sticky)
        }

        @JvmStatic
        fun emit(tag: String, actual: Any = Any(), sticky: Boolean = false) = synchronized(Apollo.get().mStickyEventMap) {
            val event = Event(tag, actual, ProcessUtil.getPid(), sticky)
            if (sticky) {
                Apollo.get().mStickyEventMap.put(tag, event)
            }
            Apollo.get().mFlowableProcessor.onNext(event)

            if (Apollo.get().mIPCEnable) {
                Apollo.get().mApolloBinderGenerator.broadcastEvent(event)
            }

        }

        @JvmStatic
        fun removeStickyEvent(vararg tags: String) = tags.forEach { tag ->
            synchronized(Apollo.get().mStickyEventMap) {
                Apollo.get().mStickyEventMap.remove(tag)
            }
        }

        @JvmStatic
        fun removeAllStickyEvent() = {
            synchronized(Apollo.get().mStickyEventMap) {
                Apollo.get().mStickyEventMap.clear()
            }
        }


        /**
         * 根据tag和eventType获取指定类型的Sticky事件
         */
        @JvmStatic
        fun <T> getStickyEvent(tag: String, eventType: Class<T>): T? {
            synchronized(Apollo.get().mStickyEventMap) {
                val o = Apollo.get().mStickyEventMap[tag]?.data as Any
                if (o.javaClass.canonicalName == eventType.canonicalName) {
                    return eventType.cast(o)
                }
            }
            return null
        }

        /**
         * 根据tag获取Sticky事件
         */
        @JvmStatic
        fun getStickyEvent(tag: String): Any? {
            synchronized(Apollo.get().mStickyEventMap) {
                return if (Apollo.get().mStickyEventMap[tag] == null) null else Apollo.get().mStickyEventMap[tag]?.data
            }
        }
    }

}