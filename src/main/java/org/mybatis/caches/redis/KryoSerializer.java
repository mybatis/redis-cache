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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * SerializeUtil with Kryo, which is faster and space consuming.
 *
 * @author Lei Jiang(ladd.cn@gmail.com)
 */
public final class KryoSerializer {

  private static Kryo kryo;
  private static Output output;
  private static Input input;
  static {
    kryo = new Kryo();
    output = new Output(200, -1);
    input = new Input();
  }

  private KryoSerializer() {
    // prevent instantiation
  }

  public static byte[] serialize(Object object) {
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
//    kryo.register(object.getClass());
    output.clear();
    kryo.writeClassAndObject(output, object);
    return output.toBytes();
  }

  public static Object unserialize(byte[] bytes) {
    input.setBuffer(bytes);
    return kryo.readClassAndObject(input);
  }

}
