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
package org.mybatis.caches.redis;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import redis.clients.jedis.JedisClusterHostAndPortMap;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisConfig extends JedisPoolConfig {

  private String hosts = Protocol.DEFAULT_HOST + ':' + Protocol.DEFAULT_PORT;
  private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
  private int soTimeout = Protocol.DEFAULT_TIMEOUT;
  private int maxAttempts = 5;
  private String password;
  private int database = Protocol.DEFAULT_DATABASE;
  private String clientName;
  private boolean ssl;
  private SSLSocketFactory sslSocketFactory;
  private SSLParameters sslParameters;
  private HostnameVerifier hostnameVerifier;
  private JedisClusterHostAndPortMap hostAndPortMap;
  private Serializer serializer = JDKSerializer.INSTANCE;

  public boolean isSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public SSLSocketFactory getSslSocketFactory() {
    return sslSocketFactory;
  }

  public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
    this.sslSocketFactory = sslSocketFactory;
  }

  public SSLParameters getSslParameters() {
    return sslParameters;
  }

  public void setSslParameters(SSLParameters sslParameters) {
    this.sslParameters = sslParameters;
  }

  public HostnameVerifier getHostnameVerifier() {
    return hostnameVerifier;
  }

  public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
    this.hostnameVerifier = hostnameVerifier;
  }

  public JedisClusterHostAndPortMap getHostAndPortMap() {
    return hostAndPortMap;
  }

  public void setHostAndPortMap(JedisClusterHostAndPortMap hostAndPortMap) {
    this.hostAndPortMap = hostAndPortMap;
  }

  public String getHosts() {
    return hosts;
  }

  public void setHosts(String hosts) {
    this.hosts = hosts;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if ("".equals(password)) {
      password = null;
    }
    this.password = password;
  }

  public int getDatabase() {
    return database;
  }

  public void setDatabase(int database) {
    this.database = database;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    if ("".equals(clientName)) {
      clientName = null;
    }
    this.clientName = clientName;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public int getSoTimeout() {
    return soTimeout;
  }

  public void setSoTimeout(int soTimeout) {
    this.soTimeout = soTimeout;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public Serializer getSerializer() {
    return serializer;
  }

  public void setSerializer(Serializer serializer) {
    this.serializer = serializer;
  }

}
