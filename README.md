# Apollo
[![](https://jitpack.io/v/lsxiao/Apollo.svg)](https://jitpack.io/#lsxiao/Apollo)
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Methods count-core: 93 | deps: 5492-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.lsxiao.Apollo%3Aapollo%3A0.1.2"><img src="https://img.shields.io/badge/Size-13 KB-e91e63.svg"/></a>


Compile-time android event bus depended on RxJava ,which support sticky event and multiple schedulers.

[中文文档](https://github.com/lsxiao/Apollo/blob/master/README-zh-CN.md)

## Demo Preview
![](https://raw.githubusercontent.com/lsxiao/Apollo/master/demo.gif?raw=true)


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
  apt "com.github.lsxiao.Apollo:processor:0.1.4-alpha"
  compile "com.github.lsxiao.Apollo:apollo:0.1.4-alpha"
  compile 'io.reactivex:rxandroid:1.2.1'//use the latest version,this just a simple.
}

```

## Usage

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

write a method where you want to receive events

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

    //the subscribeOn and observeOn support  main, io, new, computation, trampoline, immediate schedulers.
    //subscribeOn default scheduler is io.
    //observeOn default scheduler is main.
    @Receive(tag = EVENT_SHOW_USER,subscribeOn = Receive.Thread.IO, observeOn = Receive.Thread.MAIN)
    public void receiveUser(User user) {
        Log.d("apollo", "MainActivity receive user event" + user.toString());
    }

    //if you want to receive a sticky event.
    //try make type = Receive.Type.STICKY.
    //the default value is type = Receive.Type.Normal.
    @Receive(tag = EVENT_SHOW_USER,type = Receive.Type.STICKY)
    public void receiveBookSticky(Book book) {
        Log.d("apollo", "MainActivity receive book event" + book.toString());
    }

    //support multiple tag
    @Receive(tag = {TAG1,TAG2})
    public void receiveUser(User user) {
        //do something
    }

    //support non-parameter method
    @Receive(tag = TAG)
    public void receiveUser() {
        //do something
    }

    @Receive(tag = {TAG1,TAG2})
    public void receiveUser() {
        //do something
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

finally send a event where your want.

```java
 //a normal event
 Apollo.get().send(EVENT_SHOW_USER, new User("lsxiao"));

 //a non-parameter event
 Apollo.get().send(EVENT_SHOW_USER);

 //a sticky event
 Apollo.get().sendSticky(EVENT_SHOW_BOOK, new Book("A Song of Ice and Fire"));
```

## Pull Requests

welcome all pull requests.

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
