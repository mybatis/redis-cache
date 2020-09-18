/**
 *    Copyright 2015-2020 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.ibatis.cache.CacheException;

public enum JDKSerializer implements Serializer {
  // Enum singleton, which is preferred approach since Java 1.5
  INSTANCE;

  private JDKSerializer() {
    // prevent instantiation
  }

  public byte[] serialize(long timestamp, Object object) {
    try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(object);
      baos.write(longToBytes(timestamp));
      return baos.toByteArray();
    } catch (Exception e) {
      throw new CacheException(e);
    }
  }

  public long getTimestamp(byte[] bytes) {
    if (bytes == null || bytes.length < 8) {
      return -1;
    }
    byte[] copy = new byte[8];
    System.arraycopy(bytes, bytes.length - 8, copy, 0, 8);
    return bytesToLong(copy);
  }

  public Object unserialize(byte[] bytes) {
    if (bytes == null || bytes.length < 8) {
      return null;
    }
    try (ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOf(bytes, bytes.length - 8));
        ObjectInputStream ois = new ObjectInputStream(bais)) {
      return ois.readObject();
    } catch (Exception e) {
      throw new CacheException(e);
    }
  }

}
