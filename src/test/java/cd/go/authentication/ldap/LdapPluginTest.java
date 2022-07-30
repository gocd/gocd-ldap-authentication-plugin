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

package cd.go.authentication.ldap;

import cd.go.plugin.base.test_helper.annotations.JsonSource;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Base64;
import java.util.Map;

import static cd.go.plugin.base.ResourceReader.readResource;
import static cd.go.plugin.base.ResourceReader.readResourceBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class LdapPluginTest extends BaseTest {
    private LdapPlugin ldapPlugin;
    private GoPluginApiRequest request;

    @BeforeEach
    void setUp() {
        ldapPlugin = new LdapPlugin();
        ldapPlugin.initializeGoApplicationAccessor(null);
        request = mock(GoPluginApiRequest.class);
    }

    @Test
    void shouldReturnPluginIcon() {
        when(request.requestName()).thenReturn("go.cd.authorization.get-icon");
        GoPluginApiResponse response = ldapPlugin.handle(request);

        assertThat(response.responseCode()).isEqualTo(200);
        Map<String, String> responseBodyAsMap = stringToMap(response.responseBody());
        assertThat(responseBodyAsMap)
                .hasSize(2)
                .containsEntry("content_type", "image/png")
                .containsEntry("data", Base64.getEncoder().encodeToString(readResourceBytes("/gocd_72_72_icon.png")));
    }

    @Test
    void shouldReturnCapabilities() {
        when(request.requestName()).thenReturn("go.cd.authorization.get-capabilities");

        GoPluginApiResponse response = ldapPlugin.handle(request);

        assertThat(response.responseCode()).isEqualTo(200);

        assertThat(stringToMap(response.responseBody()))
                .hasSize(4)
                .containsEntry("supported_auth_type", "password")
                .containsEntry("can_search", "true")
                .containsEntry("can_authorize", "false")
                .containsEntry("can_get_user_roles", "false");
    }

    @ParameterizedTest
    @JsonSource(jsonFiles = "/auth-config-metadata.json")
    void shouldReturnAuthConfigMetadata(String expected) throws JSONException {
        when(request.requestName()).thenReturn("go.cd.authorization.auth-config.get-metadata");

        GoPluginApiResponse response = ldapPlugin.handle(request);

        assertThat(response.responseCode()).isEqualTo(200);
        assertEquals(expected, response.responseBody(), true);
    }

    @Test
    void shouldReturnAuthConfigView() {
        when(request.requestName()).thenReturn("go.cd.authorization.auth-config.get-view");
        GoPluginApiResponse response = ldapPlugin.handle(request);

        assertThat(response.responseCode()).isEqualTo(200);
        Map<String, String> responseBodyAsMap = stringToMap(response.responseBody());
        assertThat(responseBodyAsMap)
                .hasSize(1)
                .containsEntry("template", readResource("/auth_config.template.html"));
    }

    @Nested
    class ValidateAuthConfig {

        @ParameterizedTest
        @JsonSource(jsonFiles = "/validate-auth-config.json")
        void shouldBeValidWhenAllRequiredFieldsAreProvided(String validJSON) {
            when(request.requestName()).thenReturn("go.cd.authorization.auth-config.validate");
            when(request.requestBody()).thenReturn(validJSON);

            GoPluginApiResponse response = ldapPlugin.handle(request);

            assertThat(response.responseCode()).isEqualTo(200);
        }

        @ParameterizedTest
        @JsonSource(jsonFiles = {"/invalid-auth-config.json", "/missing-properties-auth-config-response-body.json"})
        void shouldBeInvalidWhenRequiredFieldsAreMissing(String invalidJSON, String expectedResponseBody) throws JSONException {
            when(request.requestName()).thenReturn("go.cd.authorization.auth-config.validate");
            when(request.requestBody()).thenReturn(invalidJSON);

            GoPluginApiResponse response = ldapPlugin.handle(request);

            assertThat(response.responseCode()).isEqualTo(200);
            assertEquals(expectedResponseBody, response.responseBody(), true);
        }

        @ParameterizedTest
        @JsonSource(jsonFiles = {
                "/auth-config-with-manager-dn-but-without-password.json",
                "/missing-password-error.json"})
        void shouldBeInvalidWhenManagerDnIsProvidedButNotPassword(String invalidJSON, String expectedResponseBody) throws JSONException {
            when(request.requestName()).thenReturn("go.cd.authorization.auth-config.validate");
            when(request.requestBody()).thenReturn(invalidJSON);

            GoPluginApiResponse response = ldapPlugin.handle(request);

            assertThat(response.responseCode()).isEqualTo(200);
            assertEquals(expectedResponseBody, response.responseBody(), true);
        }
    }
}