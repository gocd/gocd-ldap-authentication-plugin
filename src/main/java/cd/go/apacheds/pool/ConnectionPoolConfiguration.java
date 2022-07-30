/*
 * Copyright 2022 Thoughtworks, Inc.
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

import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionPoolConfiguration {
    private final GenericObjectPool.Config poolConfig;

    public ConnectionPoolConfiguration() {
        poolConfig = new GenericObjectPool.Config();
        initDefault();
    }

    private void initDefault() {
        poolConfig.lifo = true;
        poolConfig.maxActive = 250;
        poolConfig.maxIdle = 50;
        poolConfig.maxWait = -1L;
        poolConfig.minIdle = 0;
        poolConfig.numTestsPerEvictionRun = 3;
        poolConfig.softMinEvictableIdleTimeMillis = -1L;
        poolConfig.timeBetweenEvictionRunsMillis = -1L;
        poolConfig.minEvictableIdleTimeMillis = 1000L * 60L * 30L;
        poolConfig.testOnBorrow = false;
        poolConfig.testOnReturn = false;
        poolConfig.testWhileIdle = false;
        poolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
    }

    public GenericObjectPool.Config getPoolConfig() {
        return poolConfig;
    }

    public void lifo(boolean lifo) {
        this.poolConfig.lifo = lifo;
    }

    public void maxActive(int maxActive) {
        this.poolConfig.maxActive = maxActive;
    }

    public void maxIdle(int maxIdle) {
        this.poolConfig.maxIdle = maxIdle;
    }

    public void maxWait(int maxWait) {
        this.poolConfig.maxWait = maxWait;
    }

    public void minEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.poolConfig.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void minIdle(int minIdle) {
        this.poolConfig.minIdle = minIdle;
    }

    public void numTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.poolConfig.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void softMinEvictableIdleTimeMillis(int softMinEvictableIdleTimeMillis) {
        this.poolConfig.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public void testOnBorrow(boolean testOnBorrow) {
        this.poolConfig.testOnBorrow = testOnBorrow;
    }

    public void testOnReturn(boolean testOnReturn) {
        this.poolConfig.testOnReturn = testOnReturn;
    }

    public void testWhileIdle(boolean testWhileIdle) {
        this.poolConfig.testWhileIdle = testWhileIdle;
    }

    public void timeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.poolConfig.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void whenExhaustedAction(byte whenExhaustedAction) {
        this.poolConfig.whenExhaustedAction = whenExhaustedAction;
    }
}
