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

import cd.go.authentication.ldap.LdapClient;
import cd.go.authentication.ldap.LdapFactory;
import cd.go.authentication.ldap.RequestBodyMother;
import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.JNDILdapClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;

import static cd.go.authentication.ldap.RequestBodyMother.forSearchWithMultipleAuthConfigs;
import static cd.go.authentication.ldap.RequestBodyMother.forSearchWithSearchFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SearchUserExecutorTest {

    private GoPluginApiRequest request;
    private LdapFactory ldapFactory;
    private LdapClient ldapClient;

    @BeforeEach
    void setUp() {
        request = mock(GoPluginApiRequest.class);
        ldapFactory = mock(LdapFactory.class);
        ldapClient = mock(LdapClient.class);

        when(ldapFactory.ldapForConfiguration(any(LdapConfiguration.class))).thenReturn(ldapClient);
    }

    @Test
    void shouldSearchUsersUsingDefaultFilter() throws Exception {
        final String searchRequestBody = RequestBodyMother.forSearch("some-text");

        when(request.requestBody()).thenReturn(searchRequestBody);

        new SearchUserExecutor(ldapFactory).execute(request);

        ArgumentCaptor<String> filterArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(ldapClient).search(filterArgumentCaptor.capture(), eq(new String[]{"some-text"}), any(UserMapper.class), eq(100));

        final String expectedFilter = "(|(sAMAccountName=*{0}*)(uid=*{0}*)(cn=*{0}*)(mail=*{0}*)(otherMailbox=*{0}*))";
        assertThat(filterArgumentCaptor.getValue()).isEqualTo(expectedFilter);
    }

    @Test
    void shouldSearchUserUsingTheAuthConfigSearchFilter() throws Exception {
        final String searchRequestBody = forSearchWithSearchFilter("some-text", "(cn={0})");
        when(request.requestBody()).thenReturn(searchRequestBody);

        new SearchUserExecutor(ldapFactory).execute(request);

        ArgumentCaptor<String> filterArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ldapClient).search(filterArgumentCaptor.capture(), eq(new String[]{"some-text"}), any(UserMapper.class), eq(100));

        assertThat(filterArgumentCaptor.getValue()).isEqualTo("(cn={0})");
    }

    @Test
    void shouldListUsersMatchingTheSearchTerm() throws Exception {
        final String searchRequestBody = forSearchWithSearchFilter("some-text", "(cn={0})");
        when(request.requestBody()).thenReturn(searchRequestBody);

        final User user = new User("username", "displayName", "mail");
        when(ldapClient.search(any(String.class), eq(new String[]{"some-text"}), any(UserMapper.class), anyInt())).thenReturn(Arrays.asList(user));

        final GoPluginApiResponse response = new SearchUserExecutor(ldapFactory).execute(request);

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"username\": \"username\",\n" +
                "    \"display_name\": \"displayName\",\n" +
                "    \"email\": \"mail\"\n" +
                "  }\n" +
                "]";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    void shouldSearchUsersAgainstMultipleLdapServers() throws Exception {
        final String searchRequestBody = forSearchWithMultipleAuthConfigs("some-text");
        when(request.requestBody()).thenReturn(searchRequestBody);

        final User userFromAuthConfig1 = new User("username-from-auth-config-1", "displayName-1", "mail-1");
        final User userFromAuthConfig2 = new User("username-from-auth-config-2", "displayName-2", "mail-2");

        when(ldapClient.search(any(String.class), eq(new String[]{"some-text"}), any(UserMapper.class), anyInt())).thenReturn(Arrays.asList(userFromAuthConfig1)).thenReturn(Arrays.asList(userFromAuthConfig2));

        final GoPluginApiResponse response = new SearchUserExecutor(ldapFactory).execute(request);

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

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    void shouldHandleSearchFailureWhenSearchAgainstMultipleLdapServers() throws Exception {
        final String searchRequestBody = forSearchWithMultipleAuthConfigs("some-text");
        when(request.requestBody()).thenReturn(searchRequestBody);

        final User userFromAuthConfig2 = new User("username-from-auth-config-2", "displayName-2", "mail-2");

        when(ldapClient.search(any(String.class), eq(new String[]{"some-text"}), any(UserMapper.class), anyInt())).thenThrow(new RuntimeException()).thenReturn(Arrays.asList(userFromAuthConfig2));

        final GoPluginApiResponse response = new SearchUserExecutor(ldapFactory).execute(request);

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"username\": \"username-from-auth-config-2\",\n" +
                "    \"display_name\": \"displayName-2\",\n" +
                "    \"email\": \"mail-2\"\n" +
                "  }\n" +
                "]";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}