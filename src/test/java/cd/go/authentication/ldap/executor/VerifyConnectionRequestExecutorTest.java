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

import cd.go.authentication.ldap.LdapClient;
import cd.go.authentication.ldap.LdapFactory;
import cd.go.authentication.ldap.model.LdapConfiguration;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.NamingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class VerifyConnectionRequestExecutorTest {
    private LdapFactory ldapFactory;
    private LdapClient ldapClient;

    @BeforeEach
    void setUp() {
        ldapFactory = mock(LdapFactory.class);
        ldapClient = mock(LdapClient.class);
    }

    @Test
    void execute_shouldValidateTheConfiguration() throws JSONException {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(null, null, null);
        request.setRequestBody(inVaildConfig());

        GoPluginApiResponse response = new VerifyConnectionRequestExecutor().execute(request);

        String expectedResponse = "{\"message\":\"Validation failed for the given Auth Config\",\"errors\":[{\"message\":\"UserLoginFilter must not be blank.\",\"key\":\"UserLoginFilter\"}],\"status\":\"validation-failed\"}";

        assertEquals(expectedResponse, response.responseBody(), true);
    }

    @Test
    void execute_shouldVerifyConnectionForAValidConfiguration() throws Exception {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(null, null, null);
        request.setRequestBody(vaildConfig());

        when(ldapFactory.ldapForConfiguration(any(LdapConfiguration.class))).thenReturn(ldapClient);
        doThrow(new NamingException("Cannot verify connection")).when(ldapClient).validate();

        GoPluginApiResponse response = new VerifyConnectionRequestExecutor(ldapFactory).execute(request);

        String expectedResponse = "{\"message\":\"Cannot verify connection\",\"status\":\"failure\"}";
        assertThat(response.responseBody()).isEqualTo(expectedResponse);
    }

    @Test
    void execute_shouldVerifyConnection() throws Exception {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(null, null, null);
        request.setRequestBody(vaildConfig());

        when(ldapFactory.ldapForConfiguration(any(LdapConfiguration.class))).thenReturn(ldapClient);
        doNothing().when(ldapClient).validate();

        GoPluginApiResponse response = new VerifyConnectionRequestExecutor(ldapFactory).execute(request);

        String expectedResponse = "{\"message\":\"Connection ok\",\"status\":\"success\"}";
        assertThat(response.responseBody()).isEqualTo(expectedResponse);
    }

    private String vaildConfig() {
        return "{\n" +
                "    \"ManagerDN\": \"uid=admin,ou=system\",\n" +
                "    \"DisplayNameAttribute\": \"displayName\",\n" +
                "    \"SearchBases\": \"search_base\",\n" +
                "    \"UserSearchFilter\": \"attr\",\n" +
                "    \"UserLoginFilter\": \"uid\",\n" +
                "    \"Url\": \"ldap://localhost:10389\",\n" +
                "    \"Password\": \"secret\",\n" +
                "    \"EmailAttribute\": \"email\"\n" +
                "}";
    }

    private String inVaildConfig() {
        return "{\n" +
                "    \"ManagerDN\": \"uid=admin,ou=system\",\n" +
                "    \"DisplayNameAttribute\": \"displayName\",\n" +
                "    \"SearchBases\": \"search_base\",\n" +
                "    \"UserSearchFilter\": \"attr\",\n" +
                "    \"Url\": \"ldap://localhost:10389\",\n" +
                "    \"Password\": \"secret\"\n" +
                "}";
    }
}