package com.lsxiao.apollo.core.entity;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-24 10:36
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class Event {
    private String tag;
    private Object data;
    private int pid;
    private boolean isSticky = false;

    public Event() {
    }

    public Event(String tag, Object data, int pid) {
        this.tag = tag;
        this.data = data;
        this.pid = pid;
    }

    public Event(String tag, Object data, int pid, boolean isSticky) {
        this.tag = tag;
        this.data = data;
        this.pid = pid;
        this.isSticky = isSticky;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isSticky() {
        return isSticky;
    }

    public void setSticky(boolean sticky) {
        isSticky = sticky;
    }
}
