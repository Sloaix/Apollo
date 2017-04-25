# Apollo
[![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>


Best compile-time RxBus for android,which support RxJava2.

RxJava1 see this:
[Apollo English Document for RxJava1](https://github.com/lsxiao/Apollo/blob/master/README-0.x.md)

[RxJava1 Apollo中文文档](https://github.com/lsxiao/Apollo/blob/master/README-zh-CN-0.x.md)

RxJava2 Apollo中文文档(即将到来)

## Demo Preview
![](https://raw.githubusercontent.com/lsxiao/Apollo/master/demo.gif?raw=true)


## TODO

- [ ] debug feature.
- [ ] more unit test.
- [ ] AIDL.

## Including in your project
We need to include the apt plugin in our classpath to enable Annotation Processing:

```groovy
allProjects {
  repositories {
    maven { url "https://www.jitpack.io" }
  }
}
```

Add the library to the project-level build.gradle, using the apt plugin to enable Annotation Processing:


```groovy
dependencies {
  compile "io.reactivex:rxandroid:2.0.1"//use the latest version
  compile "com.github.lsxiao.Apollo:core:1.0.0-beta.1"
  annotationProcessor "com.github.lsxiao.Apollo:processor:1.0.0-beta.1"

  //for kotlin
  kapt "com.github.lsxiao.Apollo.processor:1.0.0-beta.1"
}
```

## Usage

### Init
init the Apollo in your custom application.

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //note!the ApolloBinderGeneratorImpl is generated code.
        //because Apollo is a java library and it can't depend on a android library(RxAndroid),
        //so you must provide a AndroidSchedulers.mainThread() to init.
       Apollo.init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance());
    }
}
```

### Bind/Unbind
you can bind and unbind Apollo in BaseActivity.

```java
public abstract class BaseActivity extends AppCompatActivity {
    private ApolloBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mBinder = Apollo.bind(this);
        afterCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBinder!=null){
            mBinder.unbind();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}

```

### Receive Event
write a method where you want to receive events

- default
```java
    @Receive("event")
    public void onEvent(Event event) {
       //do something.
    }
```
- non-parameter
```java
    @Receive("event")
    public void showDialog(){
        //show dialog.
    }
```

- multiple tag
```java
    @Receive({"event1","event2"})
    public void showDialog(){
        //show dialog.
    }
```

- receive event of specified times
```java
    //the event will be received twice at most.
    @Take(2)
    @Receive("event")
    public void showDialog(){
        //show dialog.
    }
```

- schedulers
```java
    //the SubscribeOn and @ObserveOn support  main, io, new, computation, trampoline, immediate schedulers.
    //@SubscribeOn default scheduler is io.
    //@ObserveOn default scheduler is main.

    @SubscribeOn(SchedulerProvider.Tag.IO)
    @ObserveOn(SchedulerProvider.Tag.MAIN)
    @Receive("event")
    public void receiveUser() {
        //do something.
    }
```

- receive sticky event,the sticky event will be auto removed when event is received.
```java
    @Sticky
    @Receive("event")
    public void receiveEvent(Event event) {
        //do something.
    }
```

- receive sticky event and not auto remove that sticky event.
```java
    @Sticky(remove = false)
    @Receive("event")
    public void receiveEvent(Event event) {
        //do something.
    }
```

- receive sticky event and remove sticky events programmatically.
```java
    @Sticky
    @Receive("event")
    public void receiveEvent(Event event) {
        //remove all
        Apollo.removeAllStickyEvent();

        //remove spectified tag event
        Apollo.removeStickyEvent("event");

        //do something.
    }
```

- receive event with backpressure strategy
```java
    //only support DROP,BUFFER,LATEST
    @Backpressure(BackpressureStrategy.DROP)
    @Receive("event")
    public void receiveEvent(Event event) {
        //remove all
        Apollo.removeAllStickyEvent();

        //remove spectified tag event
        Apollo.removeStickyEvent("event");

        //do something.
    }
```


### Send Event
finally send a event where your want.

```java
 //a normal event
 Apollo.emit(EVENT_SHOW_USER, new User("lsxiao"));

 //a non-arguments event
 Apollo.emit(EVENT_SHOW_USER);

 //a sticky event
 Apollo.emit(EVENT_SHOW_BOOK,new Object(),true);

 //a non-arguments sticky event
 Apollo.emit(EVENT_SHOW_BOOK,true);
```

## Release Note
- 1.0.0-alpha.2(2017-4-23) Full Refactoring
  - support RxJava2
  - split @Sticky @SubscribeOn @ObserveOn from @Receive
  - new annotation @Take
  - new annotation @Backpressure
  - use kotlin to implement processor(more friendly architecture).
  - use kotlin to implement core.

- 0.1.4 (2016-8-23)
  - update demo.
  - support send and receive primitive type event.(int,boolean,float,etc...)

- 0.1.4-alpha.1 (2016-8-12)
  - support receive a normal event only once.(NORAML_ONCE)
  - support receive sticky event and remove that sticky event.(STICKY_REMOVE)
  - support receive sticky event and remove all sticky events.(STICKY_REMOVE_ALL)


- 0.1.4-alpha (2016-8-11)
  - support multiple tags.
  - support non-parameter method.
  - fixed a bug in processor which may causing compile fail.


- 0.1.3 (2016-8-10)
  - avoid multiple bind the same object.
  - fixed a bug may cause unsubscribe.


- 0.1.2 (2016-8-8)
  - compile-time RxBus
  - support sticky event
  - support multiple scheduler.
  - support annotation.

## Maintained By
知乎 : [@面条](https://www.zhihu.com/people/lsxiao)

Github : [@lsxiao](https://github.com/lsxiao)


## License

    Copyright 2016 lsxiao, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
