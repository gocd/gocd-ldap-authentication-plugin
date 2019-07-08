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

package cd.go.authentication.ldap.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LdapConfigurationTest {

    @Test
    void shouldAbleToDeserializeToLdapProfile() {
        String json = "{\n" +
                "  \"ManagerDN\": \"uid=admin,ou=system\",\n" +
                "  \"DisplayNameAttribute\": \"displayName\",\n" +
                "  \"SearchBases\": \"ou=users,ou=system\n" +
                "  ou=employee,ou=system\",\n" +
                "  \"UserLoginFilter\": \"uid\",\n" +
                "  \"UserSearchFilter\": \"(cn={0})\",\n" +
                "  \"Url\": \"ldap://localhost:10389\",\n" +
                "  \"Password\": \"secret\"\n" +
                "}";

        LdapConfiguration ldapConfiguration = LdapConfiguration.fromJSON(json);

        assertThat(ldapConfiguration).isNotNull();
        assertThat(ldapConfiguration.getLdapUrlAsString()).isEqualTo("ldap://localhost:10389");
        assertThat(ldapConfiguration.getSearchBases()).containsExactly("ou=users,ou=system", "ou=employee,ou=system");
        assertThat(ldapConfiguration.getManagerDn()).isEqualTo("uid=admin,ou=system");
        assertThat(ldapConfiguration.getPassword()).isEqualTo("secret");
        assertThat(ldapConfiguration.getUserLoginFilter()).isEqualTo("uid");
        assertThat(ldapConfiguration.getDisplayNameAttribute()).isEqualTo("displayName");
        assertThat(ldapConfiguration.getEmailAttribute()).isEqualTo("mail");
        assertThat(ldapConfiguration.getUserSearchFilter()).isEqualTo("(cn={0})");
    }
}