/**
 *    Copyright 2015-2020 the original author or authors.
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
package org.mybatis.caches.redis.client;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterHostAndPortMap;
import redis.clients.jedis.JedisPool;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Milton Lai(millton.lai@gmail.com)
 */
public class RedisClientBuilder {
  private GenericRedisClient client;

  public RedisClientBuilder(GenericObjectPoolConfig poolConfig, String hosts, int connectionTimeout, int soTimeout,
      int maxAttempts, String password, int database, String clientName, boolean ssl, SSLSocketFactory sslSocketFactory,
      SSLParameters sslParameters, HostnameVerifier hostnameVerifier, JedisClusterHostAndPortMap hostAndPortMap) {

    if (hosts.indexOf(',') > 0) {
      // cluster config
      Set<HostAndPort> nodes = new LinkedHashSet<>();
      for (String host : hosts.split(",[ ]*")) {
        String[] hostParts = host.split(":");
        if ((hostParts.length != 2) || !(hostParts[1].matches("\\d+"))) {
          throw new RuntimeException("Invalid host name set for redis cluster: " + host);
        }
        nodes.add(new HostAndPort(hostParts[0], Integer.parseInt(hostParts[1])));
      }
      JedisCluster cluster = new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts, password, clientName,
          poolConfig, ssl, sslSocketFactory, sslParameters, hostnameVerifier, hostAndPortMap);
      this.client = new ClusterRedisClient(cluster);

    } else {
      // pool config
      password = (password == null || password.trim().length() == 0) ? null : password.trim();

      String[] hostParts = hosts.split(":");
      if ((hostParts.length != 2) || !(hostParts[1].matches("\\d+"))) {
        throw new RuntimeException("Invalid host name set for redis cluster: " + hosts);
      }
      JedisPool pool = new JedisPool(poolConfig, hostParts[0], Integer.parseInt(hostParts[1]), connectionTimeout,
          soTimeout, password, database, clientName, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
      this.client = new PooledRedisClient(pool);
    }
  }

  public GenericRedisClient getClient() {
    return client;
  }
}
