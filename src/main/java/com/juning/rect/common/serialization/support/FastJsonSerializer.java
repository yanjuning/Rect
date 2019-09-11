package com.juning.rect.common.serialization.support;

import com.alibaba.fastjson.JSON;
import com.juning.rect.common.serialization.Serializer;

import java.lang.reflect.Type;

/**
 * name="fastjson"
 * @author yanjun
 */
public class FastJsonSerializer implements Serializer {

    private static volatile FastJsonSerializer serializer;

    public static FastJsonSerializer getSerializer() {
        if (serializer == null) {
            synchronized (FastJsonSerializer.class) {
                if (serializer == null) {
                    serializer = new FastJsonSerializer();
                }
            }
        }
        return serializer;
    }

    public static void setSerializer(FastJsonSerializer serializer) {
        FastJsonSerializer.serializer = serializer;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(Type type, byte[] data) {
        return JSON.parseObject(data, type);
    }
}
