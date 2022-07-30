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

package cd.go.apacheds;

import cd.go.authentication.ldap.model.LdapConfiguration;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ConnectionConfigurationTest {
    @Test
    void shouldBuildLdapConnectionConfigFromLdapConfiguration() {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .withURL("ldaps://foo:389")
                .withSearchBases("searchBase")
                .withManagerDN("uid=admin,ou=system")
                .withPassword("secret")
                .build();

        final LdapConnectionConfig ldapConnectionConfig = new ConnectionConfiguration(ldapConfiguration).toLdapConnectionConfig();

        assertThat(ldapConnectionConfig.isUseSsl()).isTrue();
        assertThat(ldapConnectionConfig.getLdapHost()).isEqualTo("foo");
        assertThat(ldapConnectionConfig.getLdapPort()).isEqualTo(389);
        assertThat(ldapConnectionConfig.getName()).isEqualTo("uid=admin,ou=system");
        assertThat(ldapConnectionConfig.getCredentials()).isEqualTo("secret");
        assertThat(ldapConnectionConfig.getTrustManagers().length).isEqualTo(1);
    }


    @Test
    void shouldCheckEquality() {
        assertThat(new ConnectionConfiguration(new LdapConfigurationBuilder().build()))
                .isEqualTo(new ConnectionConfiguration(new LdapConfigurationBuilder().build()));

        assertThat(new ConnectionConfiguration(new LdapConfigurationBuilder().withURL("ldap://bar").build())
                .equals(new ConnectionConfiguration(new LdapConfigurationBuilder().withURL("ldaps://foo").build()))
        ).isFalse();
    }
}