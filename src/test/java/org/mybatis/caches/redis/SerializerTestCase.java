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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

public class SerializerTestCase {

  int max = 1000000;

  Serializer kryoSerializer;
  Serializer jdkSerializer;

  @Before
  public void setup() {
    kryoSerializer = KryoSerializer.INSTANCE;
    jdkSerializer = JDKSerializer.INSTANCE;
  }

  @Test
  public void testKryoSerialize() {
    SimpleBean rawSimpleBean = new SimpleBean();

    for (int i = 0; i != max; ++i) {
      kryoSerializer.serialize(rawSimpleBean);
    }

    byte[] serialBytes = kryoSerializer.serialize(rawSimpleBean);
    System.out.println("Byte size after kryo serialize " + serialBytes.length);
    SimpleBean unserializeSimpleBean = (SimpleBean) kryoSerializer.unserialize(serialBytes);

    for (int i = 0; i != max; ++i) {
      kryoSerializer.unserialize(serialBytes);
    }

    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  public void testKryoFallbackSerialize() {

    SimpleBean rawSimpleBean = new SimpleBean();
    byte[] serialBytes = jdkSerializer.serialize(rawSimpleBean);
    
    SimpleBean unserializeSimpleBean = (SimpleBean) kryoSerializer.unserialize(serialBytes);
    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  public void testKryoUnserializeWithoutRegistry() {
    SimpleBean rawSimpleBean = new SimpleBean();

    byte[] serialBytes = kryoSerializer.serialize(rawSimpleBean);
    Kryo kryoWithoutRegisty = new Kryo();
    Input input = new Input(serialBytes);
    SimpleBean unserializeSimpleBean = (SimpleBean) kryoWithoutRegisty.readClassAndObject(input);
    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  public void testJDKSerialize() {
    SimpleBean rawSimpleBean = new SimpleBean();

    for (int i = 0; i != max; ++i) {
      jdkSerializer.serialize(rawSimpleBean);
    }

    byte[] serialBytes = jdkSerializer.serialize(rawSimpleBean);
    System.out.println("Byte size after jdk serialize " + serialBytes.length);
    SimpleBean unserializeSimpleBean = (SimpleBean) jdkSerializer.unserialize(serialBytes);

    for (int i = 0; i != max; ++i) {
      jdkSerializer.unserialize(serialBytes);
    }

    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  public void testSerializeUtil() {
    SimpleBean rawSimpleBean = new SimpleBean();

    byte[] serialBytes = SerializeUtil.serialize(rawSimpleBean);
    SimpleBean unserializeSimpleBean = (SimpleBean) SerializeUtil.unserialize(serialBytes);

    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }
}
