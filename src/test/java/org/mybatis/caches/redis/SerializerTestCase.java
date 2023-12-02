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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerializerTestCase {

  int max = 1000000;

  Serializer kryoSerializer;
  Serializer jdkSerializer;

  @BeforeEach
  void setup() {
    kryoSerializer = KryoSerializer.INSTANCE;
    jdkSerializer = JDKSerializer.INSTANCE;
  }

  @Test
  void testKryoUnserializeNull() {
    Object obj = kryoSerializer.unserialize(null);
    assertNull(obj);
  }

  @Test
  void testJDKUnserializeNull() {
    Object obj = jdkSerializer.unserialize(null);
    assertNull(obj);
  }

  @Test
  void testKryoSerialize() {
    SimpleBeanStudentInfo rawSimpleBean = new SimpleBeanStudentInfo();

    for (int i = 0; i != max; ++i) {
      kryoSerializer.serialize(rawSimpleBean);
    }

    byte[] serialBytes = kryoSerializer.serialize(rawSimpleBean);
    SimpleBeanStudentInfo unserializeSimpleBean = (SimpleBeanStudentInfo) kryoSerializer.unserialize(serialBytes);

    for (int i = 0; i != max; ++i) {
      kryoSerializer.unserialize(serialBytes);
    }

    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  void testKryoFallbackSerialize() throws IOException {

    SimpleBeanStudentInfo rawSimpleBean = new SimpleBeanStudentInfo();
    byte[] serialBytes = jdkSerializer.serialize(rawSimpleBean);

    SimpleBeanStudentInfo unserializeSimpleBean = (SimpleBeanStudentInfo) kryoSerializer.unserialize(serialBytes);
    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  // test kryo thread safe
  @Test
  void testKryoSerializeMultiThread() throws IOException {
    for (int i = 0; i < 10000; i++) {
      new Thread(() -> {
        SimpleBeanStudentInfo rawSimpleBean = new SimpleBeanStudentInfo();
        byte[] serialBytes = kryoSerializer.serialize(rawSimpleBean);

        SimpleBeanStudentInfo unserializeSimpleBean = (SimpleBeanStudentInfo) kryoSerializer.unserialize(serialBytes);
        assertEquals(rawSimpleBean, unserializeSimpleBean);
      }).start();
    }
  }

  /**
   * SimpleBeanSerializedFile contains serialized bytes of an default object of simpleBeanCourceInfo. KryoSerializer can
   * unserialize from bytes of file derectly
   *
   * @throws IOException
   */
  @Test
  void testKryoUnserializeWithoutRegistryWithFile() throws IOException {
    SimpleBeanCourseInfo rawSimpleBean = new SimpleBeanCourseInfo();

    InputStream inputStream = SerializerTestCase.class.getClass()
        .getResourceAsStream("/simpleBeanCourseInfoSerializedFile");
    if (inputStream == null) {
      return;
    }
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[1000];

    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    buffer.flush();

    SimpleBeanCourseInfo unserializeSimpleBean = (SimpleBeanCourseInfo) kryoSerializer.unserialize(data);
    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  void testJDKSerialize() {
    SimpleBeanStudentInfo rawSimpleBean = new SimpleBeanStudentInfo();

    for (int i = 0; i != max; ++i) {
      jdkSerializer.serialize(rawSimpleBean);
    }

    byte[] serialBytes = jdkSerializer.serialize(rawSimpleBean);
    SimpleBeanStudentInfo unserializeSimpleBean = (SimpleBeanStudentInfo) jdkSerializer.unserialize(serialBytes);

    for (int i = 0; i != max; ++i) {
      jdkSerializer.unserialize(serialBytes);
    }

    assertEquals(rawSimpleBean, unserializeSimpleBean);

  }

  @Test
  void testSerializeCofig() {
    RedisConfig redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
    assertEquals(JDKSerializer.class, redisConfig.getSerializer().getClass());
  }
}
