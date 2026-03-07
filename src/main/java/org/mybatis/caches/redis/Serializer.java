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

public interface Serializer {

  /**
   * Serialize method
   *
   * @param object
   *
   * @return serialized bytes
   */
  byte[] serialize(Object object);

  /**
   * Unserialize method
   *
   * @param bytes
   *
   * @return unserialized object
   */
  Object unserialize(byte[] bytes);

  /**
   * Releases any thread-local resources held by this serializer for the current thread. Implementations that use
   * {@link ThreadLocal} storage (e.g. {@link KryoSerializer}) should override this method to call
   * {@link ThreadLocal#remove()} and prevent ClassLoader pinning / Metaspace leaks in web-container environments
   * (Tomcat, etc.) where container threads are reused across redeployments. Callers (e.g. a
   * {@code ServletContextListener}) should invoke this method for each thread that has used the serializer before the
   * application is stopped.
   */
  default void reset() {
    // no-op by default
  }

}
