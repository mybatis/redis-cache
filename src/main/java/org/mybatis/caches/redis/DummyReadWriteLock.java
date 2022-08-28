/*
 *    Copyright 2015-2022 the original author or authors.
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Iwao AVE!
 */
class DummyReadWriteLock implements ReadWriteLock {

  private Lock lock = new DummyLock();

  @Override
  public Lock readLock() {
    return lock;
  }

  @Override
  public Lock writeLock() {
    return lock;
  }

  static class DummyLock implements Lock {

    @Override
    public void lock() {
      // Not implemented
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
      // Not implemented
    }

    @Override
    public boolean tryLock() {
      return true;
    }

    @Override
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
      return true;
    }

    @Override
    public void unlock() {
      // Not implemented
    }

    @Override
    public Condition newCondition() {
      return null;
    }
  }

}
