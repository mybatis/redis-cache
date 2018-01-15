package org.mybatis.caches.redis;

import static org.junit.Assert.*;

import org.junit.Test;

public class SerializerTestCase {

	int max=1000000;
	  
	@Test
	public void testKryoSerialize() {
		SimpleBean rawSimpleBean=new SimpleBean();
		
		for(int i=0;i!=max;++i)
		{
			KryoSerializer.serialize(rawSimpleBean);
		}
			
		byte[] serialBytes=KryoSerializer.serialize(rawSimpleBean);
		System.out.println("Byte size after kryo serialize "+serialBytes.length);
		SimpleBean unserializeSimpleBean=(SimpleBean) KryoSerializer.unserialize(serialBytes);
		
		for(int i=0;i!=max;++i)
		{
			KryoSerializer.unserialize(serialBytes);
		}
		
		assertEquals(rawSimpleBean, unserializeSimpleBean);

	}
  
	@Test
	public void testJDKSerialize() {
		SimpleBean rawSimpleBean=new SimpleBean();
		
		for(int i=0;i!=max;++i)
		{
			JDKSerializer.serialize(rawSimpleBean);
		}
			
		byte[] serialBytes=JDKSerializer.serialize(rawSimpleBean);
		System.out.println("Byte size after jdk serialize "+serialBytes.length);
		SimpleBean unserializeSimpleBean=(SimpleBean) JDKSerializer.unserialize(serialBytes);
		
		for(int i=0;i!=max;++i)
		{
			JDKSerializer.unserialize(serialBytes);
		}
		
		assertEquals(rawSimpleBean, unserializeSimpleBean);

	}
	
	@Test
	public void testSerializeUtil() {
		SimpleBean rawSimpleBean=new SimpleBean();
	
		byte[] serialBytes=SerializeUtil.serialize(rawSimpleBean);
		SimpleBean unserializeSimpleBean=(SimpleBean) SerializeUtil.unserialize(serialBytes);
		
		assertEquals(rawSimpleBean, unserializeSimpleBean);

	}
}
