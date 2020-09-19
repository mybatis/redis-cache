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

import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;

/**
 * @author Milton Lai(millton.lai@gmail.com)
 */
public interface GenericRedisClient {

  Object eval(final String script, final int keyCount, final String... params);

  List<String> blpop(int timeout, String key);

  List<String> blpop(int timeout, String... keys);

  List<byte[]> blpop(int timeout, byte[]... keys);

  List<String> brpop(int timeout, String key);

  List<String> brpop(int timeout, String... keys);

  List<byte[]> brpop(int timeout, byte[]... keys);

  Long del(String key);

  Long del(String... keys);

  Long exists(String... keys);

  Boolean exists(String key);

  Long expire(byte[] key, int seconds);

  String get(String key);

  byte[] get(byte[] key);

  Long hdel(String key, String... fields);

  byte[] hget(byte[] key, byte[] field);

  Map<byte[], byte[]> hgetAll(byte[] key);

  Long hset(byte[] key, byte[] field, byte[] value);

  Long llen(String key);

  Long lpush(String key, String... strings);

  Long pttl(final String key);

  Long rpush(String key, String... strings);

  String set(String key, String value);

  String set(byte[] key, byte[] value);

  String set(String key, String value, SetParams params);

  String set(byte[] key, byte[] value, SetParams params);

  Long ttl(final String key);

  Long ttl(byte[] key);

}
