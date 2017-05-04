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

import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;

import javax.naming.NamingException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VerifyConnectionRequestExecutorTest {
    private LdapFactory ldapFactory;
    private Ldap ldap;

    @Before
    public void setUp() throws Exception {
        ldapFactory = mock(LdapFactory.class);
        ldap = mock(Ldap.class);
    }

    @Test
    public void execute_shouldValidateTheConfiguration() throws Exception {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(null, null, null);
        request.setRequestBody(inVaildConfig());

        GoPluginApiResponse response = new VerifyConnectionRequestExecutor(request).execute();

        String expectedResponse = "{\"message\":\"Validation failed for the given Auth Config\",\"errors\":[{\"message\":\"UserLoginFilter must not be blank.\",\"key\":\"UserLoginFilter\"}],\"status\":\"validation-failed\"}";

        assertThat(response.responseBody(), is(expectedResponse));
    }

    @Test
    public void execute_shouldVerifyConnectionForAValidConfiguration() throws Exception {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(null, null, null);
        request.setRequestBody(vaildConfig());

        when(ldapFactory.ldapForConfiguration(any(LdapConfiguration.class))).thenReturn(ldap);
        doThrow(new NamingException("Cannot verify connection")).when(ldap).validate();

        GoPluginApiResponse response = new VerifyConnectionRequestExecutor(request, ldapFactory).execute();

        String expectedResponse = "{\"message\":\"Cannot verify connection\",\"status\":\"failure\"}";
        assertThat(response.responseBody(), is(expectedResponse));
    }

    @Test
    public void execute_shouldVerifyConnection() throws Exception {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(null, null, null);
        request.setRequestBody(vaildConfig());

        when(ldapFactory.ldapForConfiguration(any(LdapConfiguration.class))).thenReturn(ldap);
        doNothing().when(ldap).validate();

        GoPluginApiResponse response = new VerifyConnectionRequestExecutor(request, ldapFactory).execute();

        String expectedResponse = "{\"message\":\"Connection ok\",\"status\":\"success\"}";
        assertThat(response.responseBody(), is(expectedResponse));
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