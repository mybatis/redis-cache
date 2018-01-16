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


import org.apache.ibatis.cache.CacheException;

public final class SerializeUtil {

    private SerializeUtil() {
        // prevent instantiation
    }

    public static byte[] serialize(Object object) {
    	 try {
    		//use kryo serialize first
 			return KryoSerializer.serialize(object);
 		} catch (Exception e) {
 			//if kryo serialize fails, user jdk serialize as a fallback
 			try {
 				return JDKSerializer.serialize(object);
 			} catch (CacheException cacheException ) {
 				throw cacheException;
 			}
 			
 		}
    }

    public static Object unserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
        	//use kryo unserialize first
			return KryoSerializer.unserialize(bytes);
		} catch (Exception e) {
			//if kryo unserialize fails, user jdk unserialize as a fallback
			try {
				return JDKSerializer.unserialize(bytes);
			} catch (CacheException cacheException ) {
				throw cacheException;
			}
			
		}
    }

}
