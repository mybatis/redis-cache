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

import org.junit.BeforeClass;
import org.junit.Test;
import org.mybatis.caches.annotations.CacheConfigure;
import org.mybatis.caches.rediscluster.RedisClusterCache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/*******************************************************************************
 * Copyright (C) 2012-2015 Microfountain Technology, Inc. All Rights Reserved.
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 * <p>
 * Last Modified: 16/1/7 下午9:12
 ******************************************************************************/

/**
 * Test with OSX
 * sudo install redis and configure redis cluster
 * redis cluster please reference @link http://redis.io/topics/cluster-tutorial
 * you can use Redis Desktop Manager to see the redis change @link http://redisdesktop.com/
 *
 * @version $Id$
 */
@CacheConfigure(flushInterval = 10)
public class RedisClusterTestCase {
    private static final String DEFAULT_ID = "org.mybatis.caches.redis.RedisClusterTestCase";

    private static RedisClusterCache cache;

    @BeforeClass
    public static void newCache() {
        cache = new RedisClusterCache(DEFAULT_ID);
    }

    /**
     *
     * Test redisCluster and flushInterval future
     *
     */
    @Test
    public void shouldFlushInterval() throws InterruptedException {
        // the key will be expire after 10 seconds
        cache.putObject("expire10seconds", "10");
        assertEquals("10", cache.getObject("expire10seconds"));

        // sleep 10 seconds
        Thread.sleep(10000L);

        assertNull(cache.getObject("expire10seconds"));
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

}
