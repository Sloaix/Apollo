package com.apollo.core.serialize;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-17 17:23
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public interface Serializable {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
