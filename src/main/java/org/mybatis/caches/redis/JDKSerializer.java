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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.ibatis.cache.CacheException;

public enum JDKSerializer implements Serializer {
  //Enum singleton, which is preferred approach since Java 1.5
  INSTANCE;

  private JDKSerializer() {
    // prevent instantiation
  }

  public byte[] serialize(Object object) {
    ObjectOutputStream oos = null;
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new CacheException(e);
    } finally {
      if (oos != null) {
        try {
          oos.close();
        } catch (IOException e) {
          // ignore IOException
        }
      }
    }
  }

  public Object unserialize(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    ByteArrayInputStream bais = null;
    ObjectInputStream ois = null;
    try {
      bais = new ByteArrayInputStream(bytes);
      ois = new ObjectInputStream(bais);
      return ois.readObject();
    } catch (Exception e) {
      throw new CacheException(e);
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (IOException e) {
          // ignore IOException
        }
      }
    }
  }

}
