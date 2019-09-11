package com.juning.rect.common.serialization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 * @author yanjun
 */
public class SerializerRegistry {
    private Map<String, Serializer> serializers = new ConcurrentHashMap<>();

    public boolean registerSerializer(String name, Serializer serializer) {
        if (serializers.containsKey(name)) {
            return false;
        } else {
            serializers.put(name, serializer);
            return true;
        }
    }

    public Serializer getSerializer(String name) {
        return serializers.get(name);
    }
}