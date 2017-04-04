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

package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.RequestBodyMother;
import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import cd.go.framework.ldap.filter.Filter;
import cd.go.framework.ldap.mapper.AbstractMapper;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.List;

import static cd.go.authentication.ldap.RequestBodyMother.forSearchWithMultipleAuthConfigs;
import static cd.go.authentication.ldap.RequestBodyMother.forSearchWithSearchFilter;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SearchUserExecutorTest {

    private GoPluginApiRequest request;
    private LdapFactory ldapFactory;
    private Ldap ldap;

    @Before
    public void setUp() throws Exception {
        request = mock(GoPluginApiRequest.class);
        ldapFactory = mock(LdapFactory.class);
        ldap = mock(Ldap.class);

        when(ldapFactory.ldapForConfiguration(any(LdapConfiguration.class))).thenReturn(ldap);
    }

    @Test
    public void shouldSearchUsersUsingDefaultFilter() throws Exception {
        final String searchRequestBody = RequestBodyMother.forSearch("some-text");
        final List<AuthConfig> authConfigs = AuthConfig.fromJSONList(searchRequestBody);

        when(request.requestBody()).thenReturn(searchRequestBody);

        new SearchUserExecutor(request, ldapFactory).execute();

        ArgumentCaptor<Filter> filterArgumentCaptor = ArgumentCaptor.forClass(Filter.class);
        final UserMapper userMapper = authConfigs.get(0).getConfiguration().getUserMapper();

        verify(ldap).search(filterArgumentCaptor.capture(), eq(userMapper), eq(100));

        final String expectedFilter = "(|(sAMAccountName=*some-text*)(uid=*some-text*)(cn=*some-text*)(userPrincipalName=*some-text*)(mail=*some-text*)(otherMailbox=*some-text*))";
        assertThat(filterArgumentCaptor.getValue().prepare(), is(expectedFilter));
    }

    @Test
    public void shouldSearchUserUsingTheAuthConfigSearchFilter() throws Exception {
        final String searchRequestBody = forSearchWithSearchFilter("some-text", "uid,cn");
        when(request.requestBody()).thenReturn(searchRequestBody);

        new SearchUserExecutor(request, ldapFactory).execute();

        ArgumentCaptor<Filter> filterArgumentCaptor = ArgumentCaptor.forClass(Filter.class);
        verify(ldap).search(filterArgumentCaptor.capture(), any(UserMapper.class), eq(100));

        assertThat(filterArgumentCaptor.getValue().prepare(), is("(|(uid=*some-text*)(cn=*some-text*))"));
    }

    @Test
    public void shouldListUsersMatchingTheSearchTerm() throws Exception {
        final String searchRequestBody = forSearchWithSearchFilter("some-text", "uid,cn");
        when(request.requestBody()).thenReturn(searchRequestBody);

        final User user = new User("username", "displayName", "mail");
        when(ldap.search(any(Filter.class), any(AbstractMapper.class), anyInt())).thenReturn(Arrays.asList(user));

        final GoPluginApiResponse response = new SearchUserExecutor(request, ldapFactory).execute();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"username\": \"username\",\n" +
                "    \"display_name\": \"displayName\",\n" +
                "    \"email\": \"mail\"\n" +
                "  }\n" +
                "]";

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldSearchUsersAgainstMultipleLdapServers() throws Exception {
        final String searchRequestBody = forSearchWithMultipleAuthConfigs("some-text");
        when(request.requestBody()).thenReturn(searchRequestBody);

        final User userFromAuthConfig1 = new User("username-from-auth-config-1", "displayName-1", "mail-1");
        final User userFromAuthConfig2 = new User("username-from-auth-config-2", "displayName-2", "mail-2");

        when(ldap.search(any(Filter.class), any(AbstractMapper.class), anyInt())).thenReturn(Arrays.asList(userFromAuthConfig1)).thenReturn(Arrays.asList(userFromAuthConfig2));

        final GoPluginApiResponse response = new SearchUserExecutor(request, ldapFactory).execute();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"username\": \"username-from-auth-config-2\",\n" +
                "    \"display_name\": \"displayName-2\",\n" +
                "    \"email\": \"mail-2\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"username\": \"username-from-auth-config-1\",\n" +
                "    \"display_name\": \"displayName-1\",\n" +
                "    \"email\": \"mail-1\"\n" +
                "  }\n" +
                "]";

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldHandleSearchFailureWhenSearchAgainstMultipleLdapServers() throws Exception {
        final String searchRequestBody = forSearchWithMultipleAuthConfigs("some-text");
        when(request.requestBody()).thenReturn(searchRequestBody);

        final User userFromAuthConfig2 = new User("username-from-auth-config-2", "displayName-2", "mail-2");

        when(ldap.search(any(Filter.class), any(AbstractMapper.class), anyInt())).thenThrow(new RuntimeException()).thenReturn(Arrays.asList(userFromAuthConfig2));

        final GoPluginApiResponse response = new SearchUserExecutor(request, ldapFactory).execute();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"username\": \"username-from-auth-config-2\",\n" +
                "    \"display_name\": \"displayName-2\",\n" +
                "    \"email\": \"mail-2\"\n" +
                "  }\n" +
                "]";

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}