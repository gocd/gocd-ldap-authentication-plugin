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
import cd.go.apacheds.LdapConfigurationBuilder;
import cd.go.authentication.ldap.BaseTest;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static cd.go.apacheds.pool.ConnectionPoolFactory.getLdapConnectionPool;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConnectionPoolFactoryTest extends BaseTest {

    @Test
    void shouldCreateConnectionPoolUsingConnectionConfiguration() {
        final ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(new LdapConfigurationBuilder().build());

        final LdapConnectionPool ldapConnectionPool = getLdapConnectionPool(connectionConfiguration);

        assertThat(ldapConnectionPool).isNotNull();
    }

    @Test
    void shouldCreateConnectionPoolWithDefaultPoolConfig() {
        final ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(new LdapConfigurationBuilder().build());

        final LdapConnectionPool ldapConnectionPool = getLdapConnectionPool(connectionConfiguration);

        assertThat(ldapConnectionPool).isNotNull();
        assertThat(ldapConnectionPool.getLifo()).isEqualTo(true);
        assertThat(ldapConnectionPool.getMaxTotal()).isEqualTo(250);
        assertThat(ldapConnectionPool.getMaxIdle()).isEqualTo(50);
        assertThat(ldapConnectionPool.getMinIdle()).isEqualTo(0);
        assertThat(ldapConnectionPool.getNumTestsPerEvictionRun()).isEqualTo(3);
        assertThat(ldapConnectionPool.getSoftMinEvictableIdleDuration()).isEqualTo(Duration.ofMillis(-1L));
        assertThat(ldapConnectionPool.getDurationBetweenEvictionRuns()).isEqualTo(Duration.ofMillis(-1L));
        assertThat(ldapConnectionPool.getMinEvictableIdleDuration()).isEqualTo(Duration.ofMinutes(30));
        assertThat(ldapConnectionPool.getTestOnBorrow()).isEqualTo(false);
        assertThat(ldapConnectionPool.getTestOnReturn()).isEqualTo(false);
        assertThat(ldapConnectionPool.getTestWhileIdle()).isEqualTo(false);
        assertThat(ldapConnectionPool.getBlockWhenExhausted()).isEqualTo(true);
    }

    @Test
    void shouldCacheConnectionPoolObjectForAConnectionConfiguration() {
        final ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(new LdapConfigurationBuilder().build());

        final LdapConnectionPool ldapConnectionPoolOne = getLdapConnectionPool(connectionConfiguration);
        final LdapConnectionPool ldapConnectionPoolTwo = getLdapConnectionPool(connectionConfiguration);

        assertThat(ldapConnectionPoolOne).isNotNull();
        assertThat(ldapConnectionPoolOne).isEqualTo(ldapConnectionPoolTwo);
    }

    @Test
    void shouldCreateNewLdapConnectionPoolForDifferentConnectionConfig() {
        final ConnectionConfiguration configuration = new ConnectionConfiguration(new LdapConfigurationBuilder().withURL("ldap://foo").build());
        final ConnectionConfiguration differentConfiguration = new ConnectionConfiguration(new LdapConfigurationBuilder().withURL("ldap://bar").build());

        assertNotEquals(configuration, differentConfiguration);

        final LdapConnectionPool ldapConnectionPoolOne = getLdapConnectionPool(configuration);
        final LdapConnectionPool ldapConnectionPoolTwo = getLdapConnectionPool(differentConfiguration);

        assertThat(ldapConnectionPoolOne).isNotNull();
        assertNotEquals(ldapConnectionPoolOne, ldapConnectionPoolTwo);
    }
}
