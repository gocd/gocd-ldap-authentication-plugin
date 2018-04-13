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

import cd.go.authentication.ldap.executor.SearchUserExecutor;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;

import java.util.*;

import static cd.go.authentication.ldap.utils.Util.GSON;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SearchUserIntegrationTest extends BaseIntegrationTest {

    @Test
    public void shouldSearchUser() {
        AuthConfig first = new AuthConfig("1", ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"}));

        final GoPluginApiResponse response = new SearchUserExecutor(createGoPluginApiRequest("bob", first))
                .execute();

        assertNotNull(response);
        final List<User> users = responseBody(response);
        assertThat(users, hasSize(1));
        assertThat(users, contains(new User("bford", "Bob Ford", "bford@example.com")));
    }

    @Test
    public void shouldSearchAcrossMultipleSearchBasesAndLimitTheResultTo100() {
        AuthConfig first = new AuthConfig("1", ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"}));

        final GoPluginApiResponse response = new SearchUserExecutor(createGoPluginApiRequest("user", first))
                .execute();

        assertNotNull(response);
        final List<User> users = responseBody(response);
        assertThat(users, hasSize(100));
    }

    @Test
    public void shouldSearchAcrossMultipleAuthConfigAndLimitTheResultTo100() {
        AuthConfig first = new AuthConfig("1", ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"}));
        AuthConfig second = new AuthConfig("2", ldapConfiguration(new String[]{"ou=Clients,ou=Enterprise,ou=Principal,ou=system"}));

        final GoPluginApiResponse response = new SearchUserExecutor(createGoPluginApiRequest("user", first, second))
                .execute();

        assertNotNull(response);
        final List<User> users = responseBody(response);
        assertThat(users, hasSize(100));
    }

    private List<User> responseBody(GoPluginApiResponse response) {
        return GSON.fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());
    }

    private GoPluginApiRequest createGoPluginApiRequest(String searchTerm, AuthConfig... authConfigs) {
        final Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("search_term", searchTerm);
        requestBodyMap.put("auth_configs", authConfigs.length == 0 ? Collections.emptyList() : Arrays.asList(authConfigs));

        final DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("foo", "1.0", "something");
        request.setRequestBody(new Gson().toJson(requestBodyMap));
        return request;
    }
}
