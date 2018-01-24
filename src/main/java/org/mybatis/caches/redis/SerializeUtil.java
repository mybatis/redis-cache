/**
 *    Copyright 2015-2018 the original author or authors.
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

import org.apache.ibatis.cache.CacheException;

public final class SerializeUtil {

  private static Serializer serializer;

  static {
    RedisConfig redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
    if ("kryo".equals(redisConfig.getSerializer())) {
      serializer = KryoSerializer.INSTANCE;
    } else {
      serializer = JDKSerializer.INSTANCE;
    }
  }

  private SerializeUtil() {
    // prevent instantiation
  }

  public static byte[] serialize(Object object) {
    return serializer.serialize(object);
  }

  public static Object unserialize(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    return serializer.unserialize(bytes);
  }

}
