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

import cd.go.apacheds.ConnectionConfiguration;
import cd.go.authentication.ldap.exception.LdapException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.directory.ldap.client.api.DefaultLdapConnectionFactory;
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ConnectionPoolFactory {
    private final static Map<ConnectionConfiguration, LdapConnectionPool> ldapConnectionPoolMap = new HashMap<>();
    private static final GenericObjectPoolConfig<LdapConnection> CONNECTION_POOL_CONFIGURATION = createConfig();

    private static <T> GenericObjectPoolConfig<T> createConfig() {
        GenericObjectPoolConfig<T> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setLifo(true);
        poolConfig.setMaxTotal(250);
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(0);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setMinEvictableIdleDuration(Duration.ofMinutes(30));
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    private ConnectionPoolFactory() {
    }

    public static LdapConnectionPool getLdapConnectionPool(ConnectionConfiguration configuration) {
        try {
            return ldapConnectionPool(configuration);
        } catch (Exception e) {
            throw new LdapException(e);
        }
    }

    private static LdapConnectionPool ldapConnectionPool(ConnectionConfiguration configuration) {
        LdapConnectionPool ldapConnectionPool = ldapConnectionPoolMap.get(configuration);
        if (ldapConnectionPool == null) {
            synchronized (configuration.toString().intern()) {
                ldapConnectionPool = ldapConnectionPoolMap.get(configuration);
                if (ldapConnectionPool == null) {
                    ldapConnectionPool = createLdapConnectionPool(configuration);
                    register(configuration, ldapConnectionPool);
                }
            }
        }

        return ldapConnectionPool;
    }

    private static LdapConnectionPool createLdapConnectionPool(ConnectionConfiguration configuration) {
        final DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory(configuration.toLdapConnectionConfig());
        return new LdapConnectionPool(new DefaultPoolableLdapConnectionFactory(factory), CONNECTION_POOL_CONFIGURATION);
    }

    private static void register(ConnectionConfiguration configuration, LdapConnectionPool ldapConnectionFactory) {
        ldapConnectionPoolMap.put(configuration, ldapConnectionFactory);
    }
}
