# Apollo [![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>

> EventBus by RxJava

- 基于RxJava
- 基于编译时注解技术，编译时生成绑定代码，非反射，性能0损耗
- 支持进程间通信
- 支持@Tag注解，支持多重Tag
- 支持@Sticky注解
- 支持@Take注解,接收指定次数事件
- 支持@SubscribeOn注解，指定订阅线程
- 支持@ObserveOn注解，指定观察线程
- 支持@Backpressure注解，支持3种背压策略(BackpressureStrategy.BUFFER，BackpressureStrategy.DROP，BackpressureStrategy.LATEST)
- 支持apollo core 非注解式使用

[English Document](https://github.com/lsxiao/Apollo/blob/master/README-EN.md)


## Todo
- 重写demo
- 完整的单元测试(已完成30%,见unittest module)

## 开始

用3分钟时间快速集成Apollo

### 集成

使用jitpack第三方依赖库
```groovy
allProjects {
  repositories {
    maven { url "https://www.jitpack.io" }
  }
}
```

在项目所在build.gralde添加依赖

```groovy
dependencies {
  //Apollo依赖RxAndroid2,请使用最新的版本
  implementation "io.reactivex.rxjava2:rxandroid:2.0.1"
  
  //Apollo的核心库
  implementation "com.github.lsxiao.Apollo:core:1.0.1"

  //IPC,如不需要可以不依赖
  implementation "com.github.lsxiao.Apollo:ipc:1.0.1"

  //Apollo的编译时注解处理器
  annotationProcessor "com.github.lsxiao.Apollo:processor:1.0.1"

  //如果你使用的是kotlin,请使用kapt
  kapt "com.github.lsxiao.Apollo:processor:1.0.1"
}
```

## 使用

### 初始化

 `ApolloBinderGeneratorImpl`在编译时生成。

```java
Apollo.init(AndroidSchedulers.mainThread(), this);
```

### 绑定/解绑

为了避免内存泄露,应在组件生命周期内绑定和解绑。
```java
public abstract class BaseActivity extends Activity {
    private ApolloBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        mBinder = Apollo.bind(this);
    }

    @Override
    protected void onDestroy() {
        ...
        if(mBinder != null){
            mBinder.unbind();        
        }
    }
    ...
}

```

### 发送
让发送更简单
```java
Apollo.emit("event","hello apollo")
```

### 接收
让接收更自在
```java
@Receive("event")
public void onEvent(String message){
    ...
}
```

### 进程间通信(IPC)

默认关闭
```
Apollo.init(AndroidSchedulers.mainThread(), this,true);
```

**!!!注意:**由于默认采用kryo序列化，所以任何需要在进程间传输的数据对象，自己包括其内部的成员对象都必须有一个默认的无参构造函数！！！

## 高级用法
### 注解
**注意!!!**,被注解的函数一定得是public修饰,且@Receive是必须注解，其余为可选注解。

| 注解          | 参数   | 描述                                                                                          | 默认值                     |
|---------------|--------|-----------------------------------------------------------------------------------------------|----------------------------|
| @Receive      |        | 接收一个字符串tag数组,或者单个字符串tag                                                       | 无                         |
| @Sticky       | remove | 接收后是否清除stikcy事件                                                                      | ture                       |
| @SubscribeOn  |        | 订阅所在线程                                                                                  | SchedulerProvider.Tag.IO   |
| @ObserveOn    |        | 观察所在线程                                                                                  | SchedulerProvider.Tag.MAIN |
| @Take         |        | 接收多少次事件,int型参数                                                                      | 无                         |
| @Backpressure |        | 背压策略(BackpressureStrategy.BUFFER，BackpressureStrategy.DROP，BackpressureStrategy.LATEST) | 无                         |

### 更多方法

```java
boolean sticky = true;

//只有tag
Apollo.emit("tag");
//tag和数据实体
Apollo.emit("tag","event");

//stikcy(只有被@Sticky注解的函数才能收到sticky事件)
Apollo.emit("tag","event",stikcy)

//只有tag的stikcy调用
Apollo.emit("tag",sticky)
```
**注意**，在emit一个boolean变量的时候,**正确写法**:

`Apollo.emit("tag",true,sticky)`

**错误写法**,这样直接就发送了一个不带参数的sticky事件:

~~`Apollo.emit("tag",true)`~~

### 自定义Serializer
Apollo默认采用kryo来序列化IPC数据对象，你可以提供一个Serializer来修改默认的实现。
```java
Apollo.serializer(new Serializable() {
    @NotNull
    @Override
    public byte[] serialize(@NotNull Object obj) {
        ...
    }

    @Override
    public <T> T deserialize(@NotNull byte[] data, @NotNull Class<T> clazz) {
        ...
    }
});
```

## 混淆
```
-dontwarn com.esotericsoftware.kryo.**
-dontwarn org.objenesis.instantiator.**
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
```

## 测试
全面而完善的测试是开源库质量的保证，目前测试用例正在不断完善中:)

### core module
`KryoSerializeTest`

### demo module
`unittest`

## 构建于ReactiveX之上

* [RxJava2](https://github.com/ReactiveX/RxJava) - Reactive Extensions for the JVM
* [RxAndroid2](https://github.com/ReactiveX/RxAndroid) - Reactive Extensions for Android

## 版本
我们使用 [语义化版本控制规范](http://semver.org/) 作为版本管理，有关可用的版本，请参阅此 [标签列表](https://github.com/lsxiao/Apollo/tags)。

## 作者

* **lsxiao** - [lsxiao](https://github.com/lsxiao)

更多 [贡献者](https://github.com/lsxiao/Apollo/contributors) 请参考这个项目的列表。

## 开源许可

Apache License Version 2.0
