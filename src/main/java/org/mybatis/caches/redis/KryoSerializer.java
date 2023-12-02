/*
 *    Copyright 2015-2023 the original author or authors.
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

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SerializeUtil with Kryo, which is faster and less space consuming.
 *
 * @author Lei Jiang(ladd.cn@gmail.com)
 */
public enum KryoSerializer implements Serializer {
  // Enum singleton, which is preferred approach since Java 1.5
  INSTANCE;

  /**
   * kryo is thread-unsafe, use ThreadLocal.
   */
  private ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);

  /**
   * Classes which can not resolved by default kryo serializer, which occurs very
   * rare(https://github.com/EsotericSoftware/kryo#using-standard-java-serialization) For these classes, we will use
   * fallbackSerializer(use JDKSerializer now) to resolve.
   */
  private Set<Class<?>> unnormalClassSet;

  /**
   * Hash codes of unnormal bytes which can not resolved by default kryo serializer, which will be resolved by
   * fallbackSerializer
   */
  private Set<Integer> unnormalBytesHashCodeSet;
  private Serializer fallbackSerializer;

  private KryoSerializer() {
    unnormalClassSet = ConcurrentHashMap.newKeySet();
    unnormalBytesHashCodeSet = ConcurrentHashMap.newKeySet();
    fallbackSerializer = JDKSerializer.INSTANCE;// use JDKSerializer as fallback
  }

  @Override
  public byte[] serialize(Object object) {
    if (unnormalClassSet.contains(object.getClass())) {
      // For unnormal class
      return fallbackSerializer.serialize(object);
    }

    /**
     * In the following cases: 1. This class occurs for the first time. 2. This class have occurred and can be resolved
     * by default kryo serializer
     */
    try (Output output = new Output(200, -1)) {
      kryos.get().writeClassAndObject(output, object);
      return output.toBytes();
    } catch (Exception e) {
      // For unnormal class occurred for the first time, exception will be thrown
      unnormalClassSet.add(object.getClass());
      return fallbackSerializer.serialize(object);// use fallback Serializer to resolve
    }
  }

  @Override
  public Object unserialize(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    int hashCode = Arrays.hashCode(bytes);
    if (unnormalBytesHashCodeSet.contains(hashCode)) {
      // For unnormal bytes
      return fallbackSerializer.unserialize(bytes);
    }

    /**
     * In the following cases: 1. This bytes occurs for the first time. 2. This bytes have occurred and can be resolved
     * by default kryo serializer
     */
    try (Input input = new Input()) {
      input.setBuffer(bytes);
      return kryos.get().readClassAndObject(input);
    } catch (Exception e) {
      // For unnormal bytes occurred for the first time, exception will be thrown
      unnormalBytesHashCodeSet.add(hashCode);
      return fallbackSerializer.unserialize(bytes);// use fallback Serializer to resolve
    }
  }

}
