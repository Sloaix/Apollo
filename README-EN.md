# Apollo [![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>

Inter-Process Communication , Compile-time Annotation.

Apollo , make RxBus simplified but not simple.

[English Document](https://github.com/lsxiao/Apollo/blob/master/README-EN.md)

## Start

quick integration with 3 minutes

### integration

use jitpack in your module.
```groovy
allProjects {
  repositories {
    maven { url "https://www.jitpack.io" }
  }
}
```

depend these in your build.gralde.

```groovy
dependencies {
  //Apollo依赖RxAndroid2,请使用最新的版本
  compile "io.reactivex:rxandroid:2.0.1"

  //Apollo的核心库
  compile "com.github.lsxiao.Apollo:core:1.0.0-beta.3"

  //Apollo的编译时注解处理器
  annotationProcessor "com.github.lsxiao.Apollo:processor:1.0.0-beta.3"

  //如果你使用的是kotlin,请使用kapt
  kapt "com.github.lsxiao.Apollo.processor:1.0.0-beta.3"
}
```

## Usage

### init

 `ApolloBinderGeneratorImpl`在编译时生成。

```java
Apollo.init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), this);
```

### bind/unbind

In order to avoid memory leaks, it should be bind and unbind within the component lifecycle.
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

### emit
make emit easier.
```java
Apollo.emit("event","hello apollo")
```

### receive
receive anywhere you like.
```java
@Receive("event")
public void onEvent(String message){
    ...
}
```
### ipc
No more configuration, just enjoy it.

more usage see below.

## Advanced Usage
### Annotation List
**Notice!!!**,The annotated function must be public, and @Receive is required ,others is optional.

| annotation          | parameter   | description                                                                                          | default                     |
|---------------|--------|-----------------------------------------------------------------------------------------------|----------------------------|
| @Receive      |        | string tag list, or a string                                                     |                          |
| @Sticky       | remove | is remove after receive event.                                                                      | ture                       |
| @SubscribeOn  |        | subscribe on scheduler                                                                                  | SchedulerProvider.Tag.IO   |
| @ObserveOn    |        | observe on scheduler                                                                                  | SchedulerProvider.Tag.MAIN |
| @Take         |        | how many times can be received.                                                                  |                          |
| @Backpressure |        | backpressure strategy(BackpressureStrategy.BUFFER，BackpressureStrategy.DROP，BackpressureStrategy.LATEST) |                          |

### Method

```java
boolean sticky = true;

//only tag
Apollo.emit("tag");
//tag and object
Apollo.emit("tag","event");

//stikcy(can received event which is annotated by @Sticky)
Apollo.emit("tag","event",stikcy)
//only tag and stikcy
Apollo.emit("tag",sticky)
```

## Build with ReactiveX

* [RxJava2](https://github.com/ReactiveX/RxJava) - Reactive Extensions for the JVM
* [RxAndroid2](https://github.com/ReactiveX/RxAndroid) - Reactive Extensions for Android

## How to contribute

welcome pr.

## Versioning
We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/lsxiao/Apollo/tags).

## Authors

* **lsxiao** - *Android developer* - [lsxiao](https://github.com/lsxiao)
See also the list of [contributors](https://github.com/lsxiao/Apollo/contributors) who participated in this project.

## License

Apache License Version 2.0

## Acknowledgments

* All I love, and who love me.