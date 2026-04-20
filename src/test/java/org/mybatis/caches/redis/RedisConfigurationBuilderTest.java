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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Introspector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.apache.ibatis.cache.CacheException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.caches.redis.sslconfig.TestHostnameVerifier;
import org.mybatis.caches.redis.sslconfig.TestSSLParameters;
import org.mybatis.caches.redis.sslconfig.TestSSLSocketFactory;

class RedisConfigurationBuilderTest {

  @Test
  void testDefaults() throws Exception {
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
  void test1() throws Exception {
    System.setProperty(RedisConfigurationBuilder.SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME, "test1.properties");
    RedisConfig redisConfig = RedisConfigurationBuilder.getInstance()
        .parseConfiguration(this.getClass().getClassLoader());
    assertEquals(KryoSerializer.class, redisConfig.getSerializer().getClass());
    assertTrue(redisConfig.isSsl());
    assertEquals(TestSSLSocketFactory.class, redisConfig.getSslSocketFactory().getClass());
    assertEquals(TestSSLParameters.class, redisConfig.getSslParameters().getClass());
    assertEquals(TestHostnameVerifier.class, redisConfig.getHostnameVerifier().getClass());
  }

  @Test
  void testUnknownSerializer() {
    RedisConfigurationBuilder builder = RedisConfigurationBuilder.getInstance();
    CacheException exception = assertThrows(CacheException.class,
        () -> builder.parseConfiguration(new PropertiesClassLoader("redis.serializer=unknown")));
    assertEquals("Unknown serializer: 'unknown'", exception.getMessage());
  }

  @Test
  void testInvalidSslFactoryClass() {
    RedisConfigurationBuilder builder = RedisConfigurationBuilder.getInstance();
    CacheException exception = assertThrows(CacheException.class, () -> builder
        .parseConfiguration(new PropertiesClassLoader("redis.sslSocketFactory=no.such.SSLSocketFactoryClass")));
    assertEquals("Could not instantiate class: 'no.such.SSLSocketFactoryClass'.", exception.getMessage());
  }

  @Test
  void testIOExceptionWhileReadingProperties() {
    RedisConfigurationBuilder builder = RedisConfigurationBuilder.getInstance();
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> builder.parseConfiguration(new IOExceptionClassLoader()));
    assertTrue(exception.getMessage().contains("An error occurred while reading classpath property"));
    assertTrue(exception.getCause() instanceof IOException);
  }

  @Test
  void testSetInstanceWithEmptyValueIgnored() {
    RedisConfigurationBuilder builder = RedisConfigurationBuilder.getInstance();
    RedisConfig redisConfig = builder
        .parseConfiguration(new PropertiesClassLoader("redis.sslSocketFactory=\nredis.hostnameVerifier=\n"));
    assertNull(redisConfig.getSslSocketFactory());
    assertNull(redisConfig.getHostnameVerifier());
  }

  @Test
  void testSupportedPropertiesAreMapped() {
    RedisConfigurationBuilder builder = RedisConfigurationBuilder.getInstance();
    RedisConfig redisConfig = builder.parseConfiguration(new PropertiesClassLoader("""
        not.redis.value=ignored
        redis.host=127.0.0.1
        redis.port=6380
        redis.connectionTimeout=1234
        redis.soTimeout=4567
        redis.database=3
        redis.clientName=test-client
        redis.ssl=true
        redis.maxTotal=21
        redis.maxIdle=11
        """));
    assertEquals("127.0.0.1", redisConfig.getHost());
    assertEquals(6380, redisConfig.getPort());
    assertEquals(1234, redisConfig.getConnectionTimeout());
    assertEquals(4567, redisConfig.getSoTimeout());
    assertEquals(3, redisConfig.getDatabase());
    assertEquals("test-client", redisConfig.getClientName());
    assertTrue(redisConfig.isSsl());
    assertEquals(21, redisConfig.getMaxTotal());
    assertEquals(11, redisConfig.getMaxIdle());
  }

  @Test
  void testUnsupportedPropertyType() {
    String unsupportedProperty = findUnsupportedPropertyName();
    assertNotNull(unsupportedProperty);
    RedisConfigurationBuilder builder = RedisConfigurationBuilder.getInstance();
    CacheException exception = assertThrows(CacheException.class,
        () -> builder.parseConfiguration(new PropertiesClassLoader("redis." + unsupportedProperty + "=any")));
    assertTrue(exception.getMessage().contains("Unsupported property type: '" + unsupportedProperty + "'"));
  }

  @AfterEach
  public void after() {
    System.setProperty(RedisConfigurationBuilder.SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME,
        RedisConfigurationBuilder.REDIS_RESOURCE);
  }

  private String findUnsupportedPropertyName() {
    for (Method method : RedisConfig.class.getMethods()) {
      if (!method.getName().startsWith("set") || method.getParameterCount() != 1) {
        continue;
      }
      String propertyName = Introspector.decapitalize(method.getName().substring(3));
      if ("serializer".equals(propertyName) || "sslSocketFactory".equals(propertyName)
          || "sslParameters".equals(propertyName) || "hostnameVerifier".equals(propertyName)) {
        continue;
      }
      Class<?> type = method.getParameterTypes()[0];
      if (!isSupportedPropertyType(type)) {
        return propertyName;
      }
    }
    return null;
  }

  private boolean isSupportedPropertyType(Class<?> type) {
    return String.class == type || int.class == type || Integer.class == type || long.class == type
        || Long.class == type || short.class == type || Short.class == type || byte.class == type
        || Byte.class == type || float.class == type || Float.class == type || boolean.class == type
        || Boolean.class == type || double.class == type || Double.class == type;
  }

  private static class PropertiesClassLoader extends ClassLoader {
    private final byte[] propertyBytes;

    private PropertiesClassLoader(String propertiesContent) {
      propertyBytes = propertiesContent.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
      return new ByteArrayInputStream(propertyBytes);
    }
  }

  private static class IOExceptionClassLoader extends ClassLoader {
    @Override
    public InputStream getResourceAsStream(String name) {
      return new InputStream() {
        @Override
        public int read() throws IOException {
          throw new IOException("simulated IO error");
        }
      };
    }
  }
}
