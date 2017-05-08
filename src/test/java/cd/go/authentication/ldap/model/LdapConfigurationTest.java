/*
 * Copyright 2017 ThoughtWorks, Inc.
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

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LdapConfigurationTest {

    @Test
    public void shouldAbleToDeserializeToLdapProfile() throws Exception {
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

        assertNotNull(ldapConfiguration);
        assertThat(ldapConfiguration.getLdapUrl(), is("ldap://localhost:10389"));
        assertThat(ldapConfiguration.getSearchBases(), contains("ou=users,ou=system", "ou=employee,ou=system"));
        assertThat(ldapConfiguration.getManagerDn(), is("uid=admin,ou=system"));
        assertThat(ldapConfiguration.getPassword(), is("secret"));
        assertThat(ldapConfiguration.getUserLoginFilter(), is("uid"));
        assertThat(ldapConfiguration.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapConfiguration.getEmailAttribute(), is("mail"));
        assertThat(ldapConfiguration.getUserSearchFilter(), is("(cn={0})"));
    }

    @Test
    public void shouldAbleToDeserializeToLdapProfilesMap() throws Exception {
        String json = "{\n" +
                "  \"profiles\": {\n" +
                "    \"ldap_profile_1\": {\n" +
                "      \"ManagerDN\": \"manger-cred\",\n" +
                "      \"DisplayNameAttribute\": \"displayName\",\n" +
                "      \"SearchBases\": \"base1\",\n" +
                "      \"UserSearchFilter\": \"uid\",\n" +
                "      \"UserLoginFilter\": \"uid\",\n" +
                "      \"Url\": \"url1\",\n" +
                "      \"Password\": \"secret\"\n" +
                "    },\n" +
                "    \"ldap_profile_2\": {\n" +
                "      \"ManagerDN\": \"manger-cred\",\n" +
                "      \"DisplayNameAttribute\": \"displayName\",\n" +
                "      \"SearchBases\": \"base2\",\n" +
                "      \"UserLoginFilter\": \"uid\",\n" +
                "      \"UserSearchFilter\": \"uid\",\n" +
                "      \"Url\": \"url2\",\n" +
                "      \"Password\": \"secret\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Map<String, LdapConfiguration> ldapProfileMap = LdapConfiguration.fromJSONMap(json);
        LdapConfiguration ldapConfiguration1 = ldapProfileMap.get("ldap_profile_1");
        LdapConfiguration ldapConfiguration2 = ldapProfileMap.get("ldap_profile_2");

        assertThat(ldapProfileMap.keySet(), contains("ldap_profile_1", "ldap_profile_2"));


        assertThat(ldapConfiguration1.getLdapUrl(), is("url1"));
        assertThat(ldapConfiguration1.getSearchBases(), contains("base1"));
        assertThat(ldapConfiguration1.getManagerDn(), is("manger-cred"));
        assertThat(ldapConfiguration1.getPassword(), is("secret"));
        assertThat(ldapConfiguration1.getUserLoginFilter(), is("uid"));
        assertThat(ldapConfiguration1.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapConfiguration1.getEmailAttribute(), is("mail"));

        assertThat(ldapConfiguration2.getLdapUrl(), is("url2"));
        assertThat(ldapConfiguration2.getSearchBases(), contains("base2"));
        assertThat(ldapConfiguration2.getManagerDn(), is("manger-cred"));
        assertThat(ldapConfiguration2.getPassword(), is("secret"));
        assertThat(ldapConfiguration2.getUserLoginFilter(), is("uid"));
        assertThat(ldapConfiguration2.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapConfiguration2.getEmailAttribute(), is("mail"));
    }
}