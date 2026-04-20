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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;

class DummyReadWriteLockTest {

  @Test
  void shouldReturnSameReadAndWriteLock() {
    DummyReadWriteLock readWriteLock = new DummyReadWriteLock();
    assertSame(readWriteLock.readLock(), readWriteLock.writeLock());
  }

  @Test
  void shouldBehaveAsNoOpLock() throws InterruptedException {
    DummyReadWriteLock readWriteLock = new DummyReadWriteLock();
    Lock lock = readWriteLock.readLock();

    lock.lock();
    lock.lockInterruptibly();
    assertTrue(lock.tryLock());
    assertTrue(lock.tryLock(1L, TimeUnit.MILLISECONDS));
    assertNull(lock.newCondition());
    lock.unlock();
  }
}
