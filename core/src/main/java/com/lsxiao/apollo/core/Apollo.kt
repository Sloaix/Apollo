package com.lsxiao.apollo.core


import com.lsxiao.apollo.core.contract.ApolloBinder
import com.lsxiao.apollo.core.contract.ApolloBinderGenerator
import com.lsxiao.apollo.core.entity.Event
import com.lsxiao.apollo.core.entity.SchedulerProvider
import com.lsxiao.apollo.core.serialize.KryoSerializer
import com.lsxiao.apollo.core.serialize.Serializable

/**
 * author lsxiao
 * date 2016-05-09 17:27
 */
class Apollo private constructor() {
    private val mFlowableProcessor: io.reactivex.processors.FlowableProcessor<Event> by lazy {
        io.reactivex.processors.PublishProcessor.create<Event>().toSerialized()
    }
    //用于保存stick事件
    private val mStickyEventMap: MutableMap<String, Event> = java.util.HashMap()
    //用于保存SubscriptionBinder
    private val mBindTargetMap: MutableMap<Int, ApolloBinder> = java.util.HashMap()
    private var mApolloBinderGenerator: ApolloBinderGenerator by kotlin.properties.Delegates.notNull()
    private var mSchedulerProvider: SchedulerProvider by kotlin.properties.Delegates.notNull()
    private var mContext: Any by kotlin.properties.Delegates.notNull()
    private var mSerializer: Serializable = KryoSerializer()

    companion object {
        private var sInstance: com.lsxiao.apollo.core.Apollo? = null

        /**
         * 返回一个Apollo的单例对象

         * @return Apollo
         */
        @JvmStatic
        @Synchronized
        private fun get(): com.lsxiao.apollo.core.Apollo {
            if (null == com.lsxiao.apollo.core.Apollo.Companion.sInstance) {
                com.lsxiao.apollo.core.Apollo.Companion.sInstance = com.lsxiao.apollo.core.Apollo()
            }
            return com.lsxiao.apollo.core.Apollo.Companion.sInstance as com.lsxiao.apollo.core.Apollo
        }


        @JvmStatic
        fun init(main: io.reactivex.Scheduler, binder: ApolloBinderGenerator, context: Any) {
            com.lsxiao.apollo.core.Apollo.Companion.get().mApolloBinderGenerator = binder
            com.lsxiao.apollo.core.Apollo.Companion.get().mSchedulerProvider = SchedulerProvider.Companion.create(main)
            com.lsxiao.apollo.core.Apollo.Companion.get().mContext = context
            com.lsxiao.apollo.core.Apollo.Companion.get().mApolloBinderGenerator.registerReceiver()
        }

        @JvmStatic
        fun serializer(serializer: Serializable) {
            com.lsxiao.apollo.core.Apollo.Companion.get().mSerializer = serializer
        }

        @JvmStatic
        fun getSerializer() = com.lsxiao.apollo.core.Apollo.Companion.get().mSerializer

        @Deprecated(message = "this method is not support ipc", replaceWith = ReplaceWith("use init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), getApplicationContext()) to instead"), level = DeprecationLevel.WARNING)
        @JvmStatic
        fun init(main: io.reactivex.Scheduler, binder: ApolloBinderGenerator) {
            com.lsxiao.apollo.core.Apollo.Companion.init(main, binder, Any())
        }

        /**
         * 判断是否有订阅者
         */
        @JvmStatic
        fun hasSubscribers(): Boolean {
            return com.lsxiao.apollo.core.Apollo.Companion.get().mFlowableProcessor.hasSubscribers()
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

            return com.lsxiao.apollo.core.Apollo.Companion.uniqueBind(o)
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
            return com.lsxiao.apollo.core.Apollo.Companion.get().mBindTargetMap.containsKey(uniqueId)
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
            if (com.lsxiao.apollo.core.Apollo.Companion.get().mBindTargetMap.containsKey(uniqueId)) {
                binder = com.lsxiao.apollo.core.Apollo.Companion.get().mBindTargetMap[uniqueId] as ApolloBinder
                //绑定已经解绑
                if (binder.isUnbind()) {
                    //移除已经解绑的binder
                    com.lsxiao.apollo.core.Apollo.Companion.get().mBindTargetMap.remove(uniqueId)
                    //重新绑定
                    binder = com.lsxiao.apollo.core.Apollo.Companion.get().mApolloBinderGenerator.generate(o)
                    //保存到map中
                    com.lsxiao.apollo.core.Apollo.Companion.get().mBindTargetMap.put(uniqueId, binder)
                }
            } else {
                binder = com.lsxiao.apollo.core.Apollo.Companion.get().mApolloBinderGenerator.generate(o)
                com.lsxiao.apollo.core.Apollo.Companion.get().mBindTargetMap.put(uniqueId, binder)
            }
            return binder
        }


        @JvmStatic
        fun toFlowable(tag: String): io.reactivex.Flowable<Any> {
            return com.lsxiao.apollo.core.Apollo.Companion.toFlowable(arrayOf(tag), Any::class.java)
        }

        @JvmStatic
        fun toFlowable(tags: Array<String>): io.reactivex.Flowable<Any> {
            return com.lsxiao.apollo.core.Apollo.Companion.toFlowable(tags, Any::class.java)
        }

        @JvmStatic
        fun <T> toFlowable(tags: Array<String>?, eventType: Class<T>?): io.reactivex.Flowable<T> {
            if (null == eventType) {
                throw java.lang.NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw java.lang.NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw java.lang.IllegalArgumentException("the tags must be not empty")
            }

            return com.lsxiao.apollo.core.Apollo.Companion.get().mFlowableProcessor
                    .filter { event ->
                        java.util.Arrays.asList(*tags).contains(event.tag) && eventType.isInstance(event.data)
                    }
                    .flatMap { event -> io.reactivex.Flowable.just(eventType.cast(event.data)) }
        }

        @JvmStatic
        fun toFlowableSticky(tag: String): io.reactivex.Flowable<Any> {
            return com.lsxiao.apollo.core.Apollo.Companion.toFlowableSticky(arrayOf(tag))
        }

        @JvmStatic
        fun toFlowableSticky(tags: Array<String>): io.reactivex.Flowable<Any> {
            return com.lsxiao.apollo.core.Apollo.Companion.toFlowableSticky(tags, Any::class.java)
        }

        @JvmStatic
        fun <T> toFlowableSticky(tags: Array<String>?, eventType: Class<T>?): io.reactivex.Flowable<T> {
            if (null == eventType) {
                throw java.lang.NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw java.lang.NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw java.lang.IllegalArgumentException("the tags must be not empty")
            }

            synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
                //普通事件的被观察者
                val flowable = com.lsxiao.apollo.core.Apollo.Companion.toFlowable(tags, eventType)

                val stickyEvents = java.util.ArrayList<Event>()
                for (tag in tags) {
                    //sticky事件
                    val event = com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap[tag]
                    if (event != null) {
                        com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap[tag]?.let { stickyEvents.add(it) }
                    }
                }

                if (!stickyEvents.isEmpty()) {
                    //合并事件序列
                    return io.reactivex.Flowable.fromIterable(stickyEvents)
                            .flatMap { event -> io.reactivex.Flowable.just(eventType.cast(event.data)) }.mergeWith(flowable)

                } else {
                    return flowable
                }
            }
        }

        @JvmStatic
        fun getSchedulerProvider(): SchedulerProvider = com.lsxiao.apollo.core.Apollo.Companion.get().mSchedulerProvider

        @JvmStatic
        fun getContext(): Any = com.lsxiao.apollo.core.Apollo.Companion.get().mContext


        @JvmStatic
        fun transfer(event: Event) = synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
            if (event.isSticky) {
                com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap.put(event.tag, event)
            }
            com.lsxiao.apollo.core.Apollo.Companion.get().mFlowableProcessor.onNext(event)
        }

        @JvmStatic
        fun emit(tag: String) = synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
            com.lsxiao.apollo.core.Apollo.Companion.emit(tag, Any(), false)
        }

        @JvmStatic
        fun emit(tag: String, actual: Any = Any()) = synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
            com.lsxiao.apollo.core.Apollo.Companion.emit(tag, actual, false)
        }

        @JvmStatic
        fun emit(tag: String, sticky: Boolean = false) = synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
            com.lsxiao.apollo.core.Apollo.Companion.emit(tag, Any(), sticky)
        }

        @JvmStatic
        fun emit(tag: String, actual: Any = Any(), sticky: Boolean = false) = synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
            val event = Event(tag, actual, ProcessUtil.getPid(), sticky)
            if (sticky) {
                com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap.put(tag, event)
            }
            com.lsxiao.apollo.core.Apollo.Companion.get().mFlowableProcessor.onNext(event)

            com.lsxiao.apollo.core.Apollo.Companion.get().mApolloBinderGenerator.broadcastEvent(event)
        }

        @JvmStatic
        fun removeStickyEvent(vararg tags: String) = tags.forEach { tag ->
            synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
                com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap.remove(tag)
            }
        }

        @JvmStatic
        fun removeAllStickyEvent() = {
            synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
                com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap.clear()
            }
        }


        /**
         * 根据tag和eventType获取指定类型的Sticky事件
         */
        @JvmStatic
        fun <T> getStickyEvent(tag: String, eventType: Class<T>): T? {
            synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
                val o = com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap[tag]?.data as Any
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
            synchronized(com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap) {
                return if (com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap[tag] == null) null else com.lsxiao.apollo.core.Apollo.Companion.get().mStickyEventMap[tag]?.data
            }
        }
    }

}