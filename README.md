# Apollo
[![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>


Compile-time android event bus depended on RxJava ,which support sticky event and multiple schedulers.

[中文文档](https://github.com/lsxiao/Apollo/blob/master/README-zh-CN.md)

## Demo Preview
![](https://raw.githubusercontent.com/lsxiao/Apollo/master/demo.gif?raw=true)


## TODO

- [ ] life circle bind by annotation(@Receive(tag="some event",bindUtil=ActivityEvent.DESTROY)).
- [ ] unit test.

## Including in your project
We need to include the apt plugin in our classpath to enable Annotation Processing:

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        //android annotation process tool
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

allProjects {
  repositories {
    maven { url "https://www.jitpack.io" }
  }
}
```

Add the library to the project-level build.gradle, using the apt plugin to enable Annotation Processing:


```groovy
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
  apt "com.github.lsxiao.Apollo:processor:0.1.4-alpha.2"
  compile "com.github.lsxiao.Apollo:apollo:0.1.4-alpha.2"
  compile 'io.reactivex:rxandroid:1.2.1'//use the latest version,this just a simple.
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

        //note!the SubscriberBinderImplement is generated code.
        //because Apollo is a java library and it can't depend on a android library(RxAndroid),
        //so you must provide a AndroidSchedulers.mainThread() to init.
        Apollo.get().init(SubscriberBinderImplement.instance(), AndroidSchedulers.mainThread());
    }
}
```

### Bind/Unbind
you can bind and unbind Apollo in BaseActivity.

```java
public abstract class BaseActivity extends AppCompatActivity {
    private SubscriptionBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mBinder = Apollo.get().bind(this);
        afterCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}

```

### Receive Event
write a method where you want to receive events

- default
```java
    @Receive(tag = TAG)
    public void receiveEvent(Event event) {
       //do something.
    }
```
- non-parameter
```java
    @Receive(tag = TAG)
    public void showDialog(){
        //show dialog.
    }
```

- multiple tag
```java
    @Receive(tag = {TAG1,TAG2})
    public void showDialog(){
        //show dialog.
    }
```

- receive normal event only once.
```java
    //the event will be received only once.
    @Receive(tag = TAG,type = Receive.Type.NORMAL_ONCE)
    public void showDialog(){
        //show dialog.
    }
```

- schedulers
```java
    //the subscribeOn and observeOn support  main, io, new, computation, trampoline, immediate schedulers.
    //subscribeOn default scheduler is io.
    //observeOn default scheduler is main.
    @Receive(tag = TAG,subscribeOn = Receive.Thread.IO, observeOn = Receive.Thread.MAIN)
    public void receiveUser() {
        //do something.
    }
```

- receive sticky event
```java
    @Receive(tag = TAG,type = Receive.Type.STICKY)
    public void receiveEvent(Event event) {
        //do something.
    }
```

- receive sticky event and remove that sticky event.
```java
    @Receive(tag = TAG,type = Receive.Type.STICKY_REMOVE)
    public void receiveEvent(Event event) {
        //do something.
    }
```

- receive sticky event and remove all sticky events.
```java
    @Receive(tag = TAG,type = Receive.Type.STICKY_REMOVE_ALL)
    public void receiveEvent(Event event) {
        //do something.
    }
```


### Send Event
finally send a event where your want.

```java
 //a normal event
 Apollo.get().send(EVENT_SHOW_USER, new User("lsxiao"));

 //a non-parameter event
 Apollo.get().send(EVENT_SHOW_USER);

 //a sticky event
 Apollo.get().sendSticky(EVENT_SHOW_BOOK, new Book("A Song of Ice and Fire"));
```

## Release Note

- 0.1.4-alpha.2 (2016-8-23)
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
