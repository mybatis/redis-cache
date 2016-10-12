/**
 *    Copyright 2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.redis;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

import org.mybatis.caches.annotations.CacheConfigure;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Cache adapter for Redis.
 *
 * @author Eduardo Macarron
 */
public final class RedisCache implements Cache {

    private final ReadWriteLock readWriteLock = new DummyReadWriteLock();

    private String id;

    private static JedisPool pool;

    private int flushInterval = 0;

    public RedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
        RedisConfig redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
        pool = new JedisPool(redisConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getConnectionTimeout(), redisConfig.getSoTimeout(), redisConfig.getPassword(),
                redisConfig.getDatabase(), redisConfig.getClientName());

        resolveCacheConfigure(id);
    }

    /**
     * loading CacheConfigure by id
     * if id can't convert to Class, ignore CacheConfigure
     */
    private void resolveCacheConfigure(final String id) {
        try {
            Class<?> type = Class.forName(id);
            CacheConfigure cacheConfigure = type.getAnnotation(CacheConfigure.class);
            if (null != cacheConfigure) {
                flushInterval = cacheConfigure.flushInterval();
            }
        } catch (ClassNotFoundException e) {
//            throw new IllegalArgumentException("Cache ID is not a Java Type", e);
        }
    }

    private Object execute(RedisCallback callback) {
        Jedis jedis = pool.getResource();
        try {
            return callback.doWithRedis(jedis);
        } finally {
            jedis.close();
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getSize() {
        return (Integer) execute(new RedisCallback() {
            @Override
            public Object doWithRedis(Jedis jedis) {
                Map<byte[], byte[]> result = jedis.hgetAll(id.toString().getBytes());
                return result.size();
            }
        });
    }

    /**
     * add expire setting
     * @param key
     * @param value
     */
    @Override
    public void putObject(final Object key, final Object value) {
        execute(new RedisCallback() {
            @Override
            public Object doWithRedis(Jedis jedis) {
                boolean isExists = jedis.exists(id);
                jedis.hset(id.toString().getBytes(), key.toString().getBytes(), SerializeUtil.serialize(value));
                if (!isExists && flushInterval > 0) {
                    jedis.expire(id, flushInterval);
                }
                return null;
            }
        });
    }

    @Override
    public Object getObject(final Object key) {
        return execute(new RedisCallback() {
            @Override
            public Object doWithRedis(Jedis jedis) {
                return SerializeUtil.unserialize(jedis.hget(id.toString().getBytes(), key.toString().getBytes()));
            }
        });
    }

    @Override
    public Object removeObject(final Object key) {
        return execute(new RedisCallback() {
            @Override
            public Object doWithRedis(Jedis jedis) {
                return jedis.hdel(id.toString(), key.toString());
            }
        });
    }

    @Override
    public void clear() {
        execute(new RedisCallback() {
            @Override
            public Object doWithRedis(Jedis jedis) {
                jedis.del(id.toString());
                return null;
            }
        });

    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    @Override
    public String toString() {
        return "Redis {" + id + "}";
    }

}
