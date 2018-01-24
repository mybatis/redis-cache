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

import java.util.Arrays;
import java.util.HashSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * SerializeUtil with Kryo, which is faster and more space consuming.
 *
 * @author Lei Jiang(ladd.cn@gmail.com)
 */
public enum KryoSerializer implements Serializer {
  //Enum singleton, which is preferred approach since Java 1.5
  INSTANCE;

  private Kryo kryo;
  private Output output;
  private Input input;
  /**
   * Classes which can not resolved by default kryo serializer, 
   * which occurs very rare(https://github.com/EsotericSoftware/kryo#using-standard-java-serialization)
   * For these classes, we will use fallbackSerializer(use JDKSerializer now) to resolve.
   */
  private HashSet<Class<?>> unnormalClassSet;

  /**
   * Hash codes of unnormal bytes which can not resolved by default kryo serializer,
   * which will be resolved by  fallbackSerializer
   */
  private HashSet<Integer> unnormalBytesHashCodeSet;
  private Serializer fallbackSerializer;

  private KryoSerializer() {
    kryo = new Kryo();
    output = new Output(200, -1);
    input = new Input();
    unnormalClassSet = new HashSet<Class<?>>();
    unnormalBytesHashCodeSet = new HashSet<Integer>();
    fallbackSerializer = JDKSerializer.INSTANCE;//use JDKSerializer as fallback 
  }

  public byte[] serialize(Object object) {
    output.clear();
    if (!unnormalClassSet.contains(object.getClass())) {
      /**
       * In the following cases:
       * 1. This class occurs for the first time.
       * 2. This class have occurred and can be resolved by default kryo serializer 
       */
      try {
        kryo.writeClassAndObject(output, object);
        return output.toBytes();
      } catch (Exception e) {
        // For unnormal class occurred for the first time, exception will be thrown
        unnormalClassSet.add(object.getClass());
        return fallbackSerializer.serialize(object);//use fallback Serializer to resolve
      }
    } else {
      //For unnormal class
      return fallbackSerializer.serialize(object);
    }
  }

  public Object unserialize(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    int hashCode = Arrays.hashCode(bytes);
    if (!unnormalBytesHashCodeSet.contains(hashCode)) {
      /**
       * In the following cases:
       * 1. This bytes occurs for the first time.
       * 2. This bytes have occurred and can be resolved by default kryo serializer 
       */
      try {
        input.setBuffer(bytes);
        return kryo.readClassAndObject(input);
      } catch (Exception e) {
        // For unnormal bytes occurred for the first time, exception will be thrown
        unnormalBytesHashCodeSet.add(hashCode);
        return fallbackSerializer.unserialize(bytes);//use fallback Serializer to resolve
      }
    } else {
      //For unnormal bytes
      return fallbackSerializer.unserialize(bytes);
    }
  }

}
