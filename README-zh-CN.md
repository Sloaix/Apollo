# Apollo
[![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)

依赖于RxJava的编译时Android事件总线,并且支持Sticky(粘连)事件,以及多个Rx调度器.

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
  apt "com.github.lsxiao.Apollo:processor:0.1.2"
  compile "com.github.lsxiao.Apollo:apollo:0.1.2"
  compile 'io.reactivex:rxandroid:1.2.1'//实际操作时请使用最新的rxandroid版本,这仅仅是一个示例.
}

```

## 使用方法

在你自己的Application中初始化Apollo

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

//你可以在BaseActivity基类中绑定和解绑Apollo

```java
public abstract class BaseActivity extends AppCompatActivity {
    private SubscriptionBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        afterCreate(savedInstanceState);
        mBinder = Apollo.get().bind(this);
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

在你喜欢的地方来接收事件.

```java
public class MainActivity extends BaseActivity {
    public static final String EVENT_SHOW_USER = "event_show_user";
    public static final String EVENT_SHOW_BOOK = "event_show_book";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {

    }

    @Receive(tag = EVENT_SHOW_BOOK)
    public void receiveBook(Book book) {
        Log.d("apollo", "MainActivity receive book event" + book.toString());
    }

    //subscribeOn和observeOn支持main, io, new, computation, trampoline, immediate 这些调度器.
    //subscribeOn 的默认调度器是 io.
    //observeOn 的默认调度器是 main.
    @Receive(tag = EVENT_SHOW_USER,subscribeOn = Receive.Thread.IO, observeOn = Receive.Thread.MAIN)
    public void receiveUser(User user) {
        Log.d("apollo", "MainActivity receive user event" + user.toString());
    }

    //如果你想接收一个sticky事件
    //你可以让 type = Receive.Type.STICKY
    //type的默认值是Receive.Type.Normal.
    @Receive(tag = EVENT_SHOW_USER,type = Receive.Type.STICKY)
    public void receiveBookSticky(Book book) {
        Log.d("apollo", "MainActivity receive book event" + book.toString());
    }

    public static class User {
        String name;

        public User(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class Book {
        String name;

        public Book(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}

```

最后,调用Apollo来发送一个事件

```java
 //a normal event
 Apollo.get().send(EVENT_SHOW_USER, new User("lsxiao"));

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
