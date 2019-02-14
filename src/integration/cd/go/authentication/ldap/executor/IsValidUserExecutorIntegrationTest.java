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
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.LdapFactory;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IsValidUserExecutorIntegrationTest extends BaseIntegrationTest {
    private LdapFactory ldapFactory;

    @Before
    public void setUp() throws Exception {
        ldapFactory = spy(new LdapFactory());
    }

    @Test
    public void shouldCheckWhetherUserIsValid() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});
        AuthConfig first = new AuthConfig("1", firstLdapConfig);

        assertThat(new IsValidUserRequestExecutor(createGoPluginApiRequest("user_1", first), ldapFactory).execute().responseCode(), is(200));

        assertThat(new IsValidUserRequestExecutor(createGoPluginApiRequest("user", first), ldapFactory).execute().responseCode(), is(404));

        verify(ldapFactory, times(2)).ldapForConfiguration(firstLdapConfig);
    }

    private GoPluginApiRequest createGoPluginApiRequest(String username, AuthConfig authConfig) {
        final Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("username", username);
        requestBodyMap.put("auth_config", authConfig);

        final DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("foo", "1.0", "something");
        request.setRequestBody(new Gson().toJson(requestBodyMap));
        return request;
    }
}
