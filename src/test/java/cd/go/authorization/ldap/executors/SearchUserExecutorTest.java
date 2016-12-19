/*
 * Copyright 2016 ThoughtWorks, Inc.
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

package cd.go.authorization.ldap.executors;

import cd.go.authorization.ldap.BaseTest;
import cd.go.authorization.ldap.LdapInstances;
import cd.go.authorization.ldap.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchUserExecutorTest extends BaseTest {

    private LdapInstances ldapInstances;
    private GoPluginApiRequest request;

    @Test
    public void setup() {
        super.setup();
        ldapInstances = new LdapInstances();
        request = mock(GoPluginApiRequest.class);
    }

    @Before

    @Test
    public void shouldAbleToSearchUser() throws Exception {
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        when(request.requestBody()).thenReturn(Collections.singletonMap(SearchUserExecutor.SEARCH_TERM, "bford").toString());

        GoPluginApiResponse response = new SearchUserExecutor(request, ldapInstances).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = new Gson().fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertThat(users, contains(new User("bford", "Bob Ford", "bford@example.com")));
    }

    @Test
    public void shouldAbleToSearchUserFromDifferentSearchBase() throws Exception {
        when(pluginConfig.getSearchBase()).thenReturn(Arrays.asList(new String[]{"ou=users,ou=system", "ou=employee,ou=system"}));
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        when(request.requestBody()).thenReturn(Collections.singletonMap(SearchUserExecutor.SEARCH_TERM, "jdoe").toString());

        GoPluginApiResponse response = new SearchUserExecutor(request, ldapInstances).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = new Gson().fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertThat(users, contains(new User("jdoe", "John Doe", "jdoe@example.com")));
    }

    @Test
    public void shouldReturnEmptySearchResultIfUserNotExist() throws Exception {
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        when(request.requestBody()).thenReturn(Collections.singletonMap(SearchUserExecutor.SEARCH_TERM, "jdoe").toString());

        GoPluginApiResponse response = new SearchUserExecutor(request, ldapInstances).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = new Gson().fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertTrue(users.isEmpty());
    }

    @Test
    public void shouldAbleToSearchUserBasedOnPattern() throws Exception {
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        when(request.requestBody()).thenReturn(Collections.singletonMap(SearchUserExecutor.SEARCH_TERM, "*banks").toString());

        GoPluginApiResponse response = new SearchUserExecutor(request, ldapInstances).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = new Gson().fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertThat(users, containsInAnyOrder(
                new User("pbanks", "Phillip Banks", "pbanks@example.com"),
                new User("sbanks", "Sarah Banks", "sbanks@example.com")
        ));
    }


}