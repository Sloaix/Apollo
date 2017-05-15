# Apollo [![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>

进程间通信、编译时注解。

Apollo，简约而不简单。

[English Document](https://github.com/lsxiao/Apollo/blob/master/README-EN.md)

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
  compile "io.reactivex:rxandroid:2.0.1"
  
  //Apollo的核心库
  compile "com.github.lsxiao.Apollo:core:1.0.0-beta.4"

  //ipc
  compile "com.github.lsxiao.Apollo:ipc:1.0.0-beta.4"

  //Apollo的编译时注解处理器
  annotationProcessor "com.github.lsxiao.Apollo:processor:1.0.0-beta.4"

  //如果你使用的是kotlin,请使用kapt
  kapt "com.github.lsxiao.Apollo.processor:1.0.0-beta.4"
}
```

## 使用

### 初始化

 `ApolloBinderGeneratorImpl`在编译时生成。

```java
Apollo.init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), this);
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
无需更多配置，请尽情使用。

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

### 方法

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

## 构建于ReactiveX之上

* [RxJava2](https://github.com/ReactiveX/RxJava) - Reactive Extensions for the JVM
* [RxAndroid2](https://github.com/ReactiveX/RxAndroid) - Reactive Extensions for Android

## 如何贡献代码

在这里没有太多的条条框框，只要你能让Apollo变得更好，代码review并测试通过，就可以被merge到主分支。

## 版本
我们使用 [语义化版本控制规范](http://semver.org/) 作为版本管理，有关可用的版本，请参阅此 [标签列表](https://github.com/lsxiao/Apollo/tags)。

## 作者

* **lsxiao** - *一个默默无闻的Android工程师* - [lsxiao](https://github.com/lsxiao)

更多 [贡献者](https://github.com/lsxiao/Apollo/contributors) 请参考这个项目的列表。

## 开源许可

Apache License Version 2.0

## 感谢

* 所有我爱，以及爱我的人
* 我职业生涯和生活中给与过我帮助的人。