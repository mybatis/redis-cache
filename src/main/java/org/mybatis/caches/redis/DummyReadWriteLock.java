/*
 *    Copyright 2014 the original author or authors.
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Iwao AVE!
 */
class DummyReadWriteLock implements ReadWriteLock {

    private Lock lock = new DummyLock();

    public Lock readLock() {
        return lock;
    }

    public Lock writeLock() {
        return lock;
    }

    static class DummyLock implements Lock {

        public void lock() {
            // Not implemented
        }

        public void lockInterruptibly() throws InterruptedException {
            // Not implemented
        }

        public boolean tryLock() {
            return true;
        }

        public boolean tryLock(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
            return true;
        }

        public void unlock() {
           // Not implemented
        }

        public Condition newCondition() {
            return null;
        }
    }

}
