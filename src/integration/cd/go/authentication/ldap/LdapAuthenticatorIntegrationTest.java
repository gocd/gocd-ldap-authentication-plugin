/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package cd.go.authentication.ldap;

import cd.go.authentication.ldap.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LdapAuthenticatorIntegrationTest extends BaseIntegrationTest {

    @Test
    public void shouldAuthenticateUser() {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});
        AuthConfig authConfig = new AuthConfig("auth_config", ldapConfiguration);

        final Credentials credentials = new Credentials("bford", "bob");

        final AuthenticationResponse response = new LdapAuthenticator().authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(response).isNotNull();
        assertThat(response.getUser()).isEqualTo(new User("bford", "Bob Ford", "bford@example.com"));
        assertThat(response.getConfigUsedForAuthentication()).isEqualTo(authConfig);
    }

    @Test
    public void shouldAuthenticateAgainstMultipleSearchBases() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"});

        AuthConfig authConfig = new AuthConfig("auth_config", ldapConfiguration);

        final Credentials credentials = new Credentials("sbanks", "sarah");

        final AuthenticationResponse response = new LdapAuthenticator().authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(response).isNotNull();
        assertThat(response.getUser()).isEqualTo(new User("sbanks", "S.Banks", "sbanks@example.com"));
        assertThat(response.getConfigUsedForAuthentication()).isEqualTo(authConfig);
        assertThat(response.getUser().getAttributes().get("dn").get().toString()).endsWith("ou=Clients,ou=Enterprise,ou=Principal,ou=system");
    }

    @Test
    public void shouldAuthenticateAgainstMultipleAuthConfig() throws Exception {
        AuthConfig authConfigForEmployees = new AuthConfig("auth_config_employees", ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"}));
        AuthConfig authConfigForClients = new AuthConfig("auth_config_clients", ldapConfiguration(new String[]{"ou=Clients,ou=Enterprise,ou=Principal,ou=system"}));

        final Credentials credentials = new Credentials("sbanks", "sarah");

        final AuthenticationResponse response = new LdapAuthenticator().authenticate(credentials, Arrays.asList(authConfigForEmployees, authConfigForClients));

        assertThat(response).isNotNull();
        assertThat(response.getUser()).isEqualTo(new User("sbanks", "S.Banks", "sbanks@example.com"));
        assertThat(response.getConfigUsedForAuthentication()).isEqualTo(authConfigForClients);
        assertThat(response.getUser().getAttributes().get("dn").get().toString()).endsWith("ou=Clients,ou=Enterprise,ou=Principal,ou=system");
    }

    @Test
    public void shouldReturnNullIfUserDoesNotExistInLdap() {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});
        AuthConfig authConfig = new AuthConfig("auth_config", ldapConfiguration);

        final Credentials credentials = new Credentials("foo", "bar");

        final AuthenticationResponse response = new LdapAuthenticator().authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(response).isNull();
    }

    @Test
    public void shouldReturnNullIfNoPasswordProvided() {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});
        AuthConfig authConfig = new AuthConfig("auth_config", ldapConfiguration);

        final Credentials credentials = new Credentials("nopasswd", "");

        final AuthenticationResponse response = new LdapAuthenticator().authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(response).isNull();
    }

    @Test
    public void authenticate_shouldNotPerformAuthenticationAndReturnNullWhenMultipleUsersFoundInASearchBaseForGivenUsername() {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"}, "(uid=*{0}*)");
        AuthConfig authConfig = new AuthConfig("auth_config", ldapConfiguration);

        final Credentials credentials = new Credentials("neil", "neil");

        final AuthenticationResponse response = new LdapAuthenticator().authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(response).isNull();
    }
}
