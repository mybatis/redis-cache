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
package org.mybatis.caches.rediscluster;

import org.apache.ibatis.cache.Cache;
import org.mybatis.caches.annotations.CacheConfigure;
import org.mybatis.caches.redis.DummyReadWriteLock;
import org.mybatis.caches.redis.SerializeUtil;
import redis.clients.jedis.JedisCluster;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Cache adapter for Redis.
 *
 * @author Eduardo Macarron
 */
public final class RedisClusterCache implements Cache {

    private final ReadWriteLock readWriteLock = new DummyReadWriteLock();
    private String id;
    private static JedisCluster jedisCluster;
    private int flushInterval = 0;

    public RedisClusterCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
        RedisClusterConfig clusterConfig = RedisClusterConfigurationBuilder.getInstance().parseConfiguration();
        jedisCluster = new JedisCluster(clusterConfig.getHaps(), clusterConfig.getTimeout(),
                clusterConfig.getMaxRedirections(), clusterConfig);

        resolveCacheConfigure();
    }

    /**
     * 加载自定义配置文件
     * 注意,此时的ID必须是一个准确的Java Type类型
     */
    private void resolveCacheConfigure() {
        try {
            Class<?> type = Class.forName(id);
            CacheConfigure cacheConfigure = type.getAnnotation(CacheConfigure.class);
            if (null != cacheConfigure) {
                flushInterval = cacheConfigure.flushInterval();
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cache ID is not a Java Type", e);
        }
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getSize() {
        Map<byte[], byte[]> result = jedisCluster.hgetAll(id.toString().getBytes());
        return result.size();
    }

    /**
     * 由于Mybatis BUG, 自定义Cache 无法使用委托模式,设置刷新间隔等;
     * 自己手动设置超期
     * @param key
     * @param value
     */
    @Override
    public void putObject(final Object key, final Object value) {
        boolean isExists = jedisCluster.exists(id);
        jedisCluster.hset(id.toString().getBytes(), key.toString().getBytes(), SerializeUtil.serialize(value));
        if (!isExists && flushInterval > 0) { // 设置超期时间
            jedisCluster.expire(id, flushInterval);
        }
    }

    @Override
    public Object getObject(final Object key) {
        return SerializeUtil.unserialize(jedisCluster.hget(id.toString().getBytes(), key.toString().getBytes()));
    }

    @Override
    public Object removeObject(final Object key) {
        return jedisCluster.hdel(id.toString(), key.toString());
    }

    @Override
    public void clear() {
        jedisCluster.del(id.toString());
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
