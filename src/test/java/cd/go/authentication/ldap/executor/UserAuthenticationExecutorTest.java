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

package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.LdapAuthenticator;
import cd.go.authentication.ldap.RequestBodyMother;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.Credentials;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class UserAuthenticationExecutorTest {
    private GoPluginApiRequest request;
    private LdapAuthenticator ldapAuthenticator;

    @BeforeEach
    void setup() {
        request = mock(GoPluginApiRequest.class);
        ldapAuthenticator = mock(LdapAuthenticator.class);
    }

    @Test
    void shouldAuthenticateAUser() {
        final String requestBody = RequestBodyMother.forAuthenticate("bford", "bob", "ou=users,ou=system");
        final List<AuthConfig> authConfigs = AuthConfig.fromJSONList(requestBody);
        when(request.requestBody()).thenReturn(requestBody);

        new UserAuthenticationExecutor(ldapAuthenticator).execute(request);

        verify(ldapAuthenticator).authenticate(new Credentials("bford", "bob"), authConfigs);
    }
}