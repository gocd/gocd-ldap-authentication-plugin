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

import cd.go.authentication.ldap.BaseIntegrationTest;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.IsValidUserRequest;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.LdapFactory;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class IsValidUserExecutorIntegrationTest extends BaseIntegrationTest {
    private LdapFactory ldapFactory;

    @Before
    public void setUp() {
        ldapFactory = spy(new LdapFactory());
    }

    @Test
    public void shouldCheckWhetherUserIsValid() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});
        AuthConfig first = new AuthConfig("1", firstLdapConfig);

        assertThat(new IsValidUserRequestExecutor(ldapFactory).execute(createGoPluginApiRequest("user_1", first)).responseCode()).isEqualTo(200);

        assertThat(new IsValidUserRequestExecutor(ldapFactory).execute(createGoPluginApiRequest("user", first)).responseCode()).isEqualTo(404);

        verify(ldapFactory, times(2)).ldapForConfiguration(firstLdapConfig);
    }

    @Test
    public void shouldCheckForCaseInsensitiveUsername() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});
        AuthConfig first = new AuthConfig("2", firstLdapConfig);

        assertThat(new IsValidUserRequestExecutor(ldapFactory).execute(createGoPluginApiRequest("user_1", first)).responseCode()).isEqualTo(200);
        assertThat(new IsValidUserRequestExecutor(ldapFactory).execute(createGoPluginApiRequest("UsEr_1", first)).responseCode()).isEqualTo(200);

        assertThat(new IsValidUserRequestExecutor(ldapFactory).execute(createGoPluginApiRequest("user", first)).responseCode()).isEqualTo(404);

        verify(ldapFactory, times(3)).ldapForConfiguration(firstLdapConfig);
    }

    private IsValidUserRequest createGoPluginApiRequest(String username, AuthConfig authConfig) {
        final Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("username", username);
        requestBodyMap.put("auth_config", authConfig);

        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(requestBodyMap), IsValidUserRequest.class);
    }
}
