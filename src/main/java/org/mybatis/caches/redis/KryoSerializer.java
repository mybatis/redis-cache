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

import java.io.Serializable;
import java.util.HashSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

/**
 * SerializeUtil with Kryo, which is faster and space consuming.
 *
 * @author Lei Jiang(ladd.cn@gmail.com)
 */
public final class KryoSerializer {

  private static Kryo kryo;
  private static Output output;
  private static Input input;
  private static HashSet<Class> beanClassToSerialize;//classes have been occurred
  static {
    kryo = new Kryo();
    output = new Output(200, -1);
    input = new Input();
    beanClassToSerialize = new HashSet<Class>();
  }

  private KryoSerializer() {
    // prevent instantiation
  }

  public static byte[] serialize(Object object) {
    output.clear();
    if (!beanClassToSerialize.contains(object.getClass())) {
      //A new class occurs
      try {
        /**
         * The following line is removed because it will lead to unserialize bug in the following situation:
         * Start redis-server
         * Start web application.
         * Execute query and some objects are cached. -> Class is registered
         * Stop web application.
         * Start web application. -> KryoSerializer is initialized and no Class is registered yet.
         * Execute the same query and Redis returns the cached objects as bytes.
         * KryoSerializer#unserialize() is invoked, but it fails because Class is unknown.
         */
        //  			kryo.register(object.getClass());
        kryo.writeClassAndObject(output, object);
      } catch (Exception e) {
        // if default kryo serializer fails, register  javaSerializer or externalizableSerializer as a fallback
        if (object instanceof Serializable) {
          kryo.register(object.getClass(), new JavaSerializer());
        } else if (object instanceof ExternalizableSerializer) {
          kryo.register(object.getClass(), new ExternalizableSerializer());
        }
        kryo.writeClassAndObject(output, object);
      } finally {
        beanClassToSerialize.add(object.getClass());
      }
    } else {
      //For class ever occurred, serialize directly
      kryo.writeClassAndObject(output, object);
    }
    return output.toBytes();
  }

  public static Object unserialize(byte[] bytes) {
    input.setBuffer(bytes);
    return kryo.readClassAndObject(input);
  }

}
