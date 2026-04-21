/*
 *    Copyright 2015-2026 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import org.junit.jupiter.api.Test;

import redis.clients.jedis.Protocol;

class RedisConfigTest {

  @Test
  void shouldUseDefaultHostWhenEmpty() {
    RedisConfig redisConfig = new RedisConfig();
    redisConfig.setHost(null);
    assertEquals(Protocol.DEFAULT_HOST, redisConfig.getHost());
    redisConfig.setHost("");
    assertEquals(Protocol.DEFAULT_HOST, redisConfig.getHost());
  }

  @Test
  void shouldNormalizePasswordAndClientName() {
    RedisConfig redisConfig = new RedisConfig();
    redisConfig.setPassword("");
    assertNull(redisConfig.getPassword());
    redisConfig.setPassword(null);
    assertNull(redisConfig.getPassword());
    redisConfig.setPassword("secret");
    assertEquals("secret", redisConfig.getPassword());

    redisConfig.setClientName("");
    assertNull(redisConfig.getClientName());
    redisConfig.setClientName(null);
    assertNull(redisConfig.getClientName());
    redisConfig.setClientName("app");
    assertEquals("app", redisConfig.getClientName());
  }

  @Test
  void shouldRoundTripConfigProperties() {
    RedisConfig redisConfig = new RedisConfig();
    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    SSLParameters sslParameters = new SSLParameters();
    HostnameVerifier hostnameVerifier = (host, session) -> true;

    redisConfig.setPort(6380);
    redisConfig.setConnectionTimeout(1001);
    redisConfig.setSoTimeout(1002);
    redisConfig.setDatabase(5);
    redisConfig.setSsl(true);
    redisConfig.setSslSocketFactory(socketFactory);
    redisConfig.setSslParameters(sslParameters);
    redisConfig.setHostnameVerifier(hostnameVerifier);
    redisConfig.setSerializer(KryoSerializer.INSTANCE);

    assertEquals(6380, redisConfig.getPort());
    assertEquals(1001, redisConfig.getConnectionTimeout());
    assertEquals(1002, redisConfig.getSoTimeout());
    assertEquals(5, redisConfig.getDatabase());
    assertTrue(redisConfig.isSsl());
    assertSame(socketFactory, redisConfig.getSslSocketFactory());
    assertSame(sslParameters, redisConfig.getSslParameters());
    assertSame(hostnameVerifier, redisConfig.getHostnameVerifier());
    assertSame(KryoSerializer.INSTANCE, redisConfig.getSerializer());
  }
}
