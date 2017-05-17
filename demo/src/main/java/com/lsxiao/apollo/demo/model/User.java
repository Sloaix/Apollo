package com.lsxiao.apollo.demo.model;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-24 00:04
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class User {
    String name;

    public User() {
    }

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
