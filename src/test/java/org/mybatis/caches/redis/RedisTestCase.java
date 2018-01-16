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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test with Ubuntu
 * sudo apt-get install redis-server
 * execute the test
 */
public final class RedisTestCase {

  private static final String DEFAULT_ID = "REDIS";

  private static RedisCache cache;

  @BeforeClass
  public static void newCache() {
    cache = new RedisCache(DEFAULT_ID);
  }

  @Test
  public void shouldDemonstrateCopiesAreEqual() {
    for (int i = 0; i < 1000; i++) {
      cache.putObject(i, i);
      assertEquals(i, cache.getObject(i));
    }
  }

  @Test
  public void shouldRemoveItemOnDemand() {
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  public void shouldFlushAllItemsOnDemand() {
    for (int i = 0; i < 5; i++) {
      cache.putObject(i, i);
    }
    assertNotNull(cache.getObject(0));
    assertNotNull(cache.getObject(4));
    cache.clear();
    assertNull(cache.getObject(0));
    assertNull(cache.getObject(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateCache() {
    cache = new RedisCache(null);
  }

  @Test
  public void shouldVerifyCacheId() {
    assertEquals("REDIS", cache.getId());
  }

  @Test
  public void shouldVerifyToString() {
    assertEquals("Redis {REDIS}", cache.toString());
  }

  @Test
  public void shouldDeleteExpiredCache() throws Exception {
    // set timeout to 3 secs
    cache.setTimeout(3);
    cache.putObject(0, 0);
    Thread.sleep(2000);
    cache.putObject(1, 1);
    // 2 secs : not expired yet
    assertEquals(0, cache.getObject(0));
    Thread.sleep(2000);
    // 4 secs : should be expired
    assertNull(cache.getObject(0));
    assertNull(cache.getObject(1));
    // Make sure timeout is re-set
    cache.putObject(2, 2);
    Thread.sleep(2000);
    // 2 secs : not expired yet
    cache.putObject(3, 3);
    assertEquals(2, cache.getObject(2));
    Thread.sleep(2000);
    // 4 secs : should be expired
    assertNull(cache.getObject(2));
    assertNull(cache.getObject(3));
  }
}
