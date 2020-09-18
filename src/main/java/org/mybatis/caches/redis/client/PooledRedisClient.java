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
package org.mybatis.caches.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;

/**
 * @author Milton Lai(millton.lai@gmail.com)
 */
public class PooledRedisClient implements GenericRedisClient {
  private JedisPoolAbstract pool;

  public PooledRedisClient(JedisPoolAbstract pool) {
    this.pool = pool;
  }

  @Override
  public Object eval(String script, int keyCount, String... params) {
    return execute((Jedis jedis) -> jedis.eval(script, keyCount, params));
  }

  @Override
  public List<String> blpop(int timeout, String key) {
    return (List<String>) execute((Jedis jedis) -> jedis.blpop(timeout, key));
  }

  @Override
  public List<String> blpop(int timeout, String... keys) {
    return (List<String>) execute((Jedis jedis) -> jedis.blpop(timeout, keys));
  }

  @Override
  public List<byte[]> blpop(int timeout, byte[]... keys) {
    return (List<byte[]>) execute((Jedis jedis) -> jedis.blpop(timeout, keys));
  }

  @Override
  public List<String> brpop(int timeout, String key) {
    return (List<String>) execute((Jedis jedis) -> jedis.brpop(timeout, key));
  }

  @Override
  public List<String> brpop(int timeout, String... keys) {
    return (List<String>) execute((Jedis jedis) -> jedis.brpop(timeout, keys));
  }

  @Override
  public List<byte[]> brpop(int timeout, byte[]... keys) {
    return (List<byte[]>) execute((Jedis jedis) -> jedis.brpop(timeout, keys));
  }

  @Override
  public Long del(String key) {
    return (Long) execute((Jedis jedis) -> jedis.del(key));
  }

  @Override
  public Long del(String... keys) {
    return (Long) execute((Jedis jedis) -> jedis.del(keys));
  }

  @Override
  public Long exists(String... keys) {
    return (Long) execute((Jedis jedis) -> jedis.exists(keys));
  }

  @Override
  public Boolean exists(String key) {
    return (Boolean) execute((Jedis jedis) -> jedis.exists(key));
  }

  @Override
  public Long expire(byte[] key, int seconds) {
    return (Long) execute((Jedis jedis) -> jedis.expire(key, seconds));
  }

  @Override
  public String get(String key) {
    return (String) execute((Jedis jedis) -> jedis.get(key));
  }

  @Override
  public byte[] get(byte[] key) {
    return (byte[]) execute((Jedis jedis) -> jedis.get(key));
  }

  @Override
  public Long hdel(String key, String... fields) {
    return (Long) execute((Jedis jedis) -> jedis.hdel(key, fields));
  }

  @Override
  public byte[] hget(byte[] key, byte[] field) {
    return (byte[]) execute((Jedis jedis) -> jedis.hget(key, field));
  }

  @Override
  public Map<byte[], byte[]> hgetAll(byte[] key) {
    return (Map<byte[], byte[]>) execute((Jedis jedis) -> jedis.hgetAll(key));
  }

  @Override
  public Long hset(byte[] key, byte[] field, byte[] value) {
    return (Long) execute((Jedis jedis) -> jedis.hset(key, field, value));
  }

  @Override
  public Long llen(String key) {
    return (Long) execute((Jedis jedis) -> jedis.llen(key));
  }

  @Override
  public Long lpush(String key, String... strings) {
    return (Long) execute((Jedis jedis) -> jedis.lpush(key, strings));
  }

  @Override
  public Long pttl(String key) {
    return (Long) execute((Jedis jedis) -> jedis.pttl(key));
  }

  @Override
  public Long rpush(String key, String... strings) {
    return (Long) execute((Jedis jedis) -> jedis.rpush(key, strings));
  }

  @Override
  public String set(String key, String value) {
    return (String) execute((Jedis jedis) -> jedis.set(key, value));
  }

  @Override
  public String set(byte[] key, byte[] value) {
    return (String) execute((Jedis jedis) -> jedis.set(key, value));
  }

  @Override
  public String set(String key, String value, SetParams params) {
    return (String) execute((Jedis jedis) -> jedis.set(key, value, params));
  }

  @Override
  public String set(byte[] key, byte[] value, SetParams params) {
    return (String) execute((Jedis jedis) -> jedis.set(key, value, params));
  }

  @Override
  public Long ttl(String key) {
    return (Long) execute((Jedis jedis) -> jedis.ttl(key));
  }

  @Override
  public Long ttl(byte[] key) {
    return (Long) execute((Jedis jedis) -> jedis.ttl(key));
  }

  private Object execute(RedisCallback callback) {
    try (Jedis jedis = pool.getResource()) {
      return callback.doWithRedis(jedis);
    }
  }

  public interface RedisCallback {
    Object doWithRedis(Jedis var1);
  }
}
