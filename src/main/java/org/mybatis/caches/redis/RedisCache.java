/**
 *    Copyright 2015-2020 the original author or authors.
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

import org.mybatis.caches.redis.client.GenericRedisClient;
import org.mybatis.caches.redis.client.RedisClientBuilder;

/**
 * Cache adapter for Redis.
 *
 * @author Eduardo Macarron
 */
public final class RedisCache implements Cache {

  private final ReadWriteLock readWriteLock = new DummyReadWriteLock();

  private String id;

  private static GenericRedisClient client;

  private final RedisConfig redisConfig;

  private Integer timeout;

  public RedisCache(final String id) {
    if (id == null) {
      throw new IllegalArgumentException("Cache instances require an ID");
    }
    this.id = id;
    redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
    RedisClientBuilder builder = new RedisClientBuilder(redisConfig, redisConfig.getHosts(),
        redisConfig.getConnectionTimeout(), redisConfig.getSoTimeout(), redisConfig.getMaxAttempts(),
        redisConfig.getPassword(), redisConfig.getDatabase(), redisConfig.getClientName(), redisConfig.isSsl(),
        redisConfig.getSslSocketFactory(), redisConfig.getSslParameters(), redisConfig.getHostnameVerifier(),
        redisConfig.getHostAndPortMap());
    client = builder.getClient();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int getSize() {
    Map<byte[], byte[]> result = client.hgetAll(id.getBytes());
    return result.size();
  }

  @Override
  public void putObject(final Object key, final Object value) {
    final byte[] idBytes = id.getBytes();
    long ts = 0;
    if (timeout != null) {
      ts = System.currentTimeMillis() + timeout * 1000;
    }
    final byte[] objBytes = redisConfig.getSerializer().serialize(ts, value);
    client.hset(idBytes, key.toString().getBytes(), objBytes);
  }

  @Override
  public Object getObject(final Object key) {
    byte[] objBytes = client.hget(id.getBytes(), key.toString().getBytes());
    if (objBytes == null || objBytes.length < 8) return null;
    long ts = redisConfig.getSerializer().getTimestamp(objBytes);
    if (ts > 0 && ts < System.currentTimeMillis()) {
      client.hdel(id, key.toString());
      return null;
    } else {
      return redisConfig.getSerializer().unserialize(objBytes);
    }
  }

  @Override
  public Object removeObject(final Object key) {
    return client.hdel(id, key.toString());
  }

  @Override
  public void clear() {
    client.del(id);
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return readWriteLock;
  }

  @Override
  public String toString() {
    return "Redis {" + id + "}";
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

}
