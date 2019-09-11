package com.juning.rect.common.serialization;

import java.lang.reflect.Type;

/**
 * @author yanjun
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param type
     * @param data
     * @param <T>
     * @return
     */
    <T> T deserialize(Type type, byte[] data);
}
