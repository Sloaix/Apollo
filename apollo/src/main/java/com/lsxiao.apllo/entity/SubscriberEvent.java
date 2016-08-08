package com.lsxiao.apllo.entity;

/**
 * author lsxiao
 * date 2016-08-07 20:00
 */
public class SubscriberEvent {
    private final String tag;
    private final Object data;
    private final Boolean sticky;

    public SubscriberEvent(String tag, Object data, boolean sticky) {
        this.tag = tag;
        this.data = data;
        this.sticky = sticky;
    }

    public SubscriberEvent(String tag, Object data) {
        this(tag, data, false);
    }

    public Object getData() {
        return data;
    }

    public String getTag() {
        return tag;
    }

    public Boolean isSticky() {
        return sticky;
    }
}
