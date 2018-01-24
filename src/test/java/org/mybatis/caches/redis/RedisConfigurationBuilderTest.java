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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.mybatis.caches.redis.sslconfig.TestHostnameVerifier;
import org.mybatis.caches.redis.sslconfig.TestSSLParameters;
import org.mybatis.caches.redis.sslconfig.TestSSLSocketFactory;

public class RedisConfigurationBuilderTest {

  @Test
  public void testDefaults() throws Exception {
    System.setProperty(RedisConfigurationBuilder.SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME, "no-such-file.properties");
    RedisConfig redisConfig = RedisConfigurationBuilder.getInstance()
        .parseConfiguration(this.getClass().getClassLoader());
    assertEquals(JDKSerializer.class, redisConfig.getSerializer().getClass());
    assertFalse(redisConfig.isSsl());
    assertNull(redisConfig.getSslSocketFactory());
    assertNull(redisConfig.getSslParameters());
    assertNull(redisConfig.getHostnameVerifier());
  }

  @Test
  public void test1() throws Exception {
    System.setProperty(RedisConfigurationBuilder.SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME, "test1.properties");
    RedisConfig redisConfig = RedisConfigurationBuilder.getInstance()
        .parseConfiguration(this.getClass().getClassLoader());
    assertEquals(KryoSerializer.class, redisConfig.getSerializer().getClass());
    assertTrue(redisConfig.isSsl());
    assertEquals(TestSSLSocketFactory.class, redisConfig.getSslSocketFactory().getClass());
    assertEquals(TestSSLParameters.class, redisConfig.getSslParameters().getClass());
    assertEquals(TestHostnameVerifier.class, redisConfig.getHostnameVerifier().getClass());
  }

  @After
  public void after() {
    System.setProperty(RedisConfigurationBuilder.SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME,
        RedisConfigurationBuilder.REDIS_RESOURCE);
  }
}
