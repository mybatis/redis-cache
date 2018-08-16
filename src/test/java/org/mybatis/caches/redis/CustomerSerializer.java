package org.mybatis.caches.redis;

public class CustomerSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return new byte[0];
    }

    @Override
    public Object unserialize(byte[] bytes) {
        return null;
    }
}
