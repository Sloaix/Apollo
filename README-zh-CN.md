# Apollo
[![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>


依赖于RxJava的编译时Android事件总线,并且支持Sticky(粘连)事件,以及多个Rx调度器.

## 示例预览
![](https://raw.githubusercontent.com/lsxiao/Apollo/master/demo.gif?raw=true)

## 引入Apollo到项目中
我们需要引入一个apt插件到我们的classpath来开启注解处理功能.

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        //Android注解处理工具
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

allProjects {
  repositories {
    maven { url "https://www.jitpack.io" }
  }
}
```

增加apt插件到项目的build.gradle配置文件中,使用apt插件来开启注解处理功能.

```groovy
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
  apt "com.github.lsxiao.Apollo:processor:0.1.4-alpha.1"
  compile "com.github.lsxiao.Apollo:apollo:0.1.4-alpha.1"
  compile 'io.reactivex:rxandroid:1.2.1'//实际操作时请使用最新的rxandroid版本,这仅仅是一个示例.
}

```

## 使用方法

### Init
在Application中初始化Apollo.

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //注意!SubscriberBinderImplement 是由Apollo在编译时生成的代码.
        //因为Apollo是java库,所以无法依赖于Android库(RxAndroid).
        //所以你必须提供一个AndroidSchedulers.mainThread()调度器来初始化Apollo.
        Apollo.get().init(SubscriberBinderImplement.instance(), AndroidSchedulers.mainThread());
    }
}
```

### 绑定/解绑
你可以在基类中来绑定和解绑Apollo.

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

### 接收事件
在你喜欢的地方来接收事件.

- 默认使用
```java
    @Receive(tag = TAG)
    public void receiveEvent(Event event) {
       //do something.
    }
```
- 无参使用
```java
    @Receive(tag = TAG)
    public void showDialog(){
        //show dialog.
    }
```

- 多个Tag
```java
    @Receive(tag = {TAG1,TAG2})
    public void showDialog(){
        //show dialog.
    }
```

- 只接受一次普通事件
```java
    //the event will be received only once.
    @Receive(tag = TAG,type = Receive.Type.NORMAL_ONCE)
    public void showDialog(){
        //show dialog.
    }
```

- 调度器
```java
    //the subscribeOn and observeOn support  main, io, new, computation, trampoline, immediate schedulers.
    //subscribeOn default scheduler is io.
    //observeOn default scheduler is main.
    @Receive(tag = TAG,subscribeOn = Receive.Thread.IO, observeOn = Receive.Thread.MAIN)
    public void receiveUser() {
        //do something.
    }
```

- 接收sticky事件
```java
    @Receive(tag = TAG,type = Receive.Type.STICKY)
    public void receiveEvent(Event event) {
        //do something.
    }
```

- 接收后清除该sticky事件
```java
    @Receive(tag = TAG,type = Receive.Type.STICKY_REMOVE)
    public void receiveEvent(Event event) {
        //do something.
    }
```

- 接收后清除所有的sticky事件
```java
    @Receive(tag = TAG,type = Receive.Type.STICKY_REMOVE_ALL)
    public void receiveEvent(Event event) {
        //do something.
    }
```


### 发送事件
finally send a event where your want.

```java
 //a normal event
 Apollo.get().send(EVENT_SHOW_USER, new User("lsxiao"));

 //a non-parameter event
 Apollo.get().send(EVENT_SHOW_USER);

 //a sticky event
 Apollo.get().sendSticky(EVENT_SHOW_BOOK, new Book("A Song of Ice and Fire"));
```

## Pull Requests(请求代码合并)

欢迎所有的 pull requests.

## 维护人
知乎 : [@面条](https://www.zhihu.com/people/lsxiao)

Github : [@lsxiao](https://github.com/lsxiao)


## 开源许可

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
