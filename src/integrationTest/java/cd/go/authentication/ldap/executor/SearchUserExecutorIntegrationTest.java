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

package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.BaseIntegrationTest;
import cd.go.authentication.ldap.LdapFactory;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.SearchUserRequest;
import cd.go.authentication.ldap.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static cd.go.authentication.ldap.utils.Util.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ApplyLdifFiles(value = "users.ldif", clazz = BaseIntegrationTest.class)
@CreateLdapServer(transports = {
        @CreateTransport(protocol = "LDAP")
})
public class SearchUserExecutorIntegrationTest extends BaseIntegrationTest {
    private LdapFactory ldapFactory;
    private SearchUserExecutor searchUserExecutor;

    @BeforeEach
    public void setUp() {
        ldapFactory = spy(new LdapFactory());
        searchUserExecutor = new SearchUserExecutor(ldapFactory);
    }

    @Test
    public void shouldSearchUser() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});
        AuthConfig first = new AuthConfig("1", firstLdapConfig);

        final GoPluginApiResponse response = searchUserExecutor.execute(createGoPluginApiRequest("bob", first));

        assertThat(response).isNotNull();
        final List<User> users = responseBody(response);
        assertThat(users).hasSize(1);
        assertThat(users).containsExactly(new User("bford", "Bob Ford", "bford@example.com"));

        verify(ldapFactory).ldapForConfiguration(firstLdapConfig);
    }

    @Test
    public void shouldSearchAcrossMultipleSearchBasesAndLimitTheResultTo100() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"});
        AuthConfig first = new AuthConfig("1", firstLdapConfig);

        final GoPluginApiResponse response = searchUserExecutor.execute(createGoPluginApiRequest("user", first));

        assertThat(response).isNotNull();
        final List<User> users = responseBody(response);
        assertThat(users).hasSize(100);

        verify(ldapFactory).ldapForConfiguration(firstLdapConfig);
    }

    @Test
    public void shouldSearchAcrossMultipleAuthConfigAndLimitTheResultTo100() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});
        final LdapConfiguration secondLdapConfig = ldapConfiguration(new String[]{"ou=Clients,ou=Enterprise,ou=Principal,ou=system"});
        AuthConfig first = new AuthConfig("1", firstLdapConfig);
        AuthConfig second = new AuthConfig("2", secondLdapConfig);

        final GoPluginApiResponse response = searchUserExecutor
                .execute(createGoPluginApiRequest("user", first, second));

        assertThat(response).isNotNull();
        final List<User> users = responseBody(response);
        assertThat(users).hasSize(100);

        verify(ldapFactory).ldapForConfiguration(firstLdapConfig);
        verify(ldapFactory).ldapForConfiguration(secondLdapConfig);
    }

    @Test
    public void shouldStopSearchWhenSearchLimitIsReached() {
        final LdapConfiguration firstLdapConfig = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});
        final LdapConfiguration secondLdapConfig = ldapConfiguration(new String[]{"ou=Clients,ou=Enterprise,ou=Principal,ou=system"});
        final LdapConfiguration thirdLdapConfig = ldapConfiguration(new String[]{"ou=Dummy,ou=Enterprise,ou=Principal,ou=system"});

        AuthConfig first = new AuthConfig("1", firstLdapConfig);
        AuthConfig second = new AuthConfig("2", secondLdapConfig);
        AuthConfig third = new AuthConfig("3", thirdLdapConfig);

        final GoPluginApiResponse response = searchUserExecutor.execute(createGoPluginApiRequest("user", first, second, third));

        assertThat(response).isNotNull();

        final List<User> users = responseBody(response);
        assertThat(users).hasSize(100);

        verify(ldapFactory).ldapForConfiguration(firstLdapConfig);
        verify(ldapFactory).ldapForConfiguration(secondLdapConfig);
        verify(ldapFactory, never()).ldapForConfiguration(thirdLdapConfig);
    }

    private List<User> responseBody(GoPluginApiResponse response) {
        return GSON.fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());
    }

    private SearchUserRequest createGoPluginApiRequest(String searchTerm, AuthConfig... authConfigs) {
        final Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("search_term", searchTerm);
        requestBodyMap.put("auth_configs", authConfigs.length == 0 ? Collections.emptyList() : Arrays.asList(authConfigs));

        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(requestBodyMap), SearchUserRequest.class);
    }
}

