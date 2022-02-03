/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.apacheds.pool;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import static org.apache.commons.pool2.impl.BaseObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

public class ConnectionPoolConfiguration {
    private final GenericObjectPoolConfig poolConfig;

    public ConnectionPoolConfiguration() {
        poolConfig = new GenericObjectPoolConfig();
        initDefault();
    }

    private void initDefault() {
        poolConfig.setLifo(true);
        poolConfig.setMaxTotal(250);
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxWaitMillis(-1L);
        poolConfig.setMinIdle(0);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setSoftMinEvictableIdleTimeMillis(-1L);
        poolConfig.setTimeBetweenEvictionRunsMillis(-1L);
        poolConfig.setMinEvictableIdleTimeMillis(1000L * 60L * 30L);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setBlockWhenExhausted(DEFAULT_BLOCK_WHEN_EXHAUSTED);
    }

    public GenericObjectPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void lifo(boolean lifo) {
        this.poolConfig.setLifo(lifo);
    }

    public void maxActive(int maxActive) {
        this.poolConfig.setMaxTotal(maxActive);
    }

    public void maxIdle(int maxIdle) {
        this.poolConfig.setMaxIdle(maxIdle);
    }

    public void maxWait(int maxWait) {
        this.poolConfig.setMaxWaitMillis(maxWait);
    }

    public void minEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public void minIdle(int minIdle) {
        this.poolConfig.setMinIdle(minIdle);
    }

    public void numTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    public void softMinEvictableIdleTimeMillis(int softMinEvictableIdleTimeMillis) {
        this.poolConfig.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
    }

    public void testOnBorrow(boolean testOnBorrow) {
        this.poolConfig.setTestOnBorrow(testOnBorrow);
    }

    public void testOnReturn(boolean testOnReturn) {
        this.poolConfig.setTestOnReturn(testOnReturn);
    }

    public void testWhileIdle(boolean testWhileIdle) {
        this.poolConfig.setTestWhileIdle(testWhileIdle);
    }

    public void timeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public void whenExhaustedAction(Boolean whenExhaustedAction) {
        this.poolConfig.setBlockWhenExhausted(whenExhaustedAction);
    }
}
