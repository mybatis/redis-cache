/**
 *    Copyright 2015-2017 the original author or authors.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * Converter from the Config to a proper {@link RedisConfig}.
 *
 * @author Eduardo Macarron
 */
final class RedisConfigurationBuilder {

    /**
     * This class instance.
     */
    private static final RedisConfigurationBuilder INSTANCE = new RedisConfigurationBuilder();

    private static final String SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME = "redis.properties.filename";

    private static final String REDIS_RESOURCE = "redis.properties";

    private final String redisPropertiesFilename;

    /**
     * Hidden constructor, this class can't be instantiated.
     */
    private RedisConfigurationBuilder() {
        redisPropertiesFilename = System.getProperty(SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME, REDIS_RESOURCE);
    }

    /**
     * Return this class instance.
     *
     * @return this class instance.
     */
    public static RedisConfigurationBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Parses the Config and builds a new {@link RedisConfig}.
     *
     * @return the converted {@link RedisConfig}.
     */
    public RedisConfig parseConfiguration() {
        return parseConfiguration(getClass().getClassLoader());
    }

    /**
     * Parses the Config and builds a new {@link RedisConfig}.
     *
     * @param the
     *            {@link ClassLoader} used to load the
     *            {@code memcached.properties} file in classpath.
     * @return the converted {@link RedisConfig}.
     */
    public RedisConfig parseConfiguration(ClassLoader classLoader) {
        Properties config = new Properties();

        InputStream input = classLoader.getResourceAsStream(redisPropertiesFilename);
        if (input != null) {
            try {
                config.load(input);
            } catch (IOException e) {
                throw new RuntimeException(
                        "An error occurred while reading classpath property '"
                                + redisPropertiesFilename
                                + "', see nested exceptions", e);
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    // close quietly
                }
            }
        }

        RedisConfig jedisConfig = new RedisConfig();
        setConfigProperties(config, jedisConfig);
        return jedisConfig;
    }

    private void setConfigProperties(Properties properties,
            RedisConfig jedisConfig) {
        if (properties != null) {
            MetaObject metaCache = SystemMetaObject.forObject(jedisConfig);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (metaCache.hasSetter(name)) {
                    Class<?> type = metaCache.getSetterType(name);
                    if (String.class == type) {
                        metaCache.setValue(name, value);
                    } else if (int.class == type || Integer.class == type) {
                        metaCache.setValue(name, Integer.valueOf(value));
                    } else if (long.class == type || Long.class == type) {
                        metaCache.setValue(name, Long.valueOf(value));
                    } else if (short.class == type || Short.class == type) {
                        metaCache.setValue(name, Short.valueOf(value));
                    } else if (byte.class == type || Byte.class == type) {
                        metaCache.setValue(name, Byte.valueOf(value));
                    } else if (float.class == type || Float.class == type) {
                        metaCache.setValue(name, Float.valueOf(value));
                    } else if (boolean.class == type || Boolean.class == type) {
                        metaCache.setValue(name, Boolean.valueOf(value));
                    } else if (double.class == type || Double.class == type) {
                        metaCache.setValue(name, Double.valueOf(value));
                    } else {
                        throw new CacheException("Unsupported property type: '"
                                + name + "' of type " + type);
                    }
                }
            }
        }
    }

}
