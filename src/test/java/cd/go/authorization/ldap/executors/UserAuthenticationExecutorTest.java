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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAuthenticationExecutorTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private LdapInstances ldapInstances;
    private GoPluginApiRequest request;

    @Test
    public void setup() {
        super.setup();
        ldapInstances = new LdapInstances();
        request = mock(GoPluginApiRequest.class);
    }

    @Test
    public void shouldAbleToAuthenticateUser() throws Exception {
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        when(request.requestBody()).thenReturn(getUserDetailMap("bford", "bob").toString());

        GoPluginApiResponse response = new UserAuthenticationExecutor(request, ldapInstances).execute();

        assertThat(response.responseCode(), is(200));
        User user = getUser(response);
        assertThat(user, is(new User("bford", "Bob Ford", "bford@example.com")));
    }

    @Test
    public void shouldFailedToAuthenticateIfUserIsInDifferentSearchBase() throws Exception {
        String username = "jdoe";

        when(pluginConfig.getSearchBase()).thenReturn(Arrays.asList(new String[]{"ou=users,ou=system"}));
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        when(request.requestBody()).thenReturn(getUserDetailMap(username, "secret").toString());

        GoPluginApiResponse response = new UserAuthenticationExecutor(request, ldapInstances).execute();

        assertThat("{}", is(response.responseBody()));

    }

    @Test
    public void shouldAbleToAuthenticateUserAgainstMultipleSearchBases() throws Exception {
        when(pluginConfig.getSearchBase()).thenReturn(Arrays.asList(new String[]{"ou=users,ou=system", "ou=employee,ou=system"}));
        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));

        when(request.requestBody()).thenReturn(getUserDetailMap("bford", "bob").toString());
        GoPluginApiResponse response = new UserAuthenticationExecutor(request, ldapInstances).execute();
        User bob = getUser(response);
        assertThat(bob, is(new User("bford", "Bob Ford", "bford@example.com")));

        when(request.requestBody()).thenReturn(getUserDetailMap("jdoe", "john").toString());
        response = new UserAuthenticationExecutor(request, ldapInstances).execute();
        User jdoe = getUser(response);
        assertThat(jdoe, is(new User("jdoe", "John Doe", "jdoe@example.com")));
    }

    private Map<String, String> getUserDetailMap(String username, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        return map;
    }

    private User getUser(GoPluginApiResponse response) {
        Type type = new TypeToken<Map<String, User>>() {
        }.getType();
        Map<String, User> map = new Gson().fromJson(response.responseBody(), type);

        return map.get("user");
    }
}
