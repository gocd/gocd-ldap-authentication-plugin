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

package cd.go.authentication.ldap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import static cd.go.authentication.ldap.executor.RequestFromServer.*;
import static cd.go.authentication.ldap.utils.Util.readResource;
import static cd.go.authentication.ldap.utils.Util.readResourceBytes;
import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LdapPluginIntegrationTest extends BaseIntegrationTest {

    private LdapPlugin ldapPlugin;

    @Before
    public void setUp() throws Exception {
        ldapPlugin = new LdapPlugin();

    }

    @Test
    public void shouldHandleGetPluginIconRequestAndReturnPluginIcon() throws Exception {
        final GoPluginApiResponse response = ldapPlugin.handle(buildRequest(REQUEST_GET_PLUGIN_ICON.requestName()));

        final String expectedJSON = format("{\"content_type\":\"image/png\",\"data\":\"%s\"}", Base64.encodeBase64String(readResourceBytes("/gocd_72_72_icon.png")));

        assertNotNull(response.responseCode());
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldHandleGetCapabilitiesRequestAndReturnPluginCapabilities() throws Exception {
        final GoPluginApiResponse response = ldapPlugin.handle(buildRequest(REQUEST_GET_CAPABILITIES.requestName()));

        final String expectedJSON = "{\n" +
                "  \"can_search\": true,\n" +
                "  \"can_authorize\": false,\n" +
                "  \"can_get_user_roles\": false,\n" +
                "  \"supported_auth_type\": \"password\"\n" +
                "}";

        assertNotNull(response.responseCode());
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldHandleGetAuthConfigMetadata() throws Exception {
        final GoPluginApiResponse response = ldapPlugin.handle(buildRequest(REQUEST_GET_AUTH_CONFIG_METADATA.requestName()));

        final String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"Url\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"SearchBases\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"ManagerDN\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"Password\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": true\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"UserSearchFilter\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"UserLoginFilter\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"DisplayNameAttribute\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"EmailAttribute\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  }\n" +
                "]";

        assertNotNull(response.responseCode());
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldHandleGetAuthConfigViewRequestAndReturnAuthConfigTemplate() throws Exception {
        final GoPluginApiResponse response = ldapPlugin.handle(buildRequest(REQUEST_AUTH_CONFIG_VIEW.requestName()));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("template", readResource("/auth_config.template.html"));

        String expectedJSON = new Gson().toJson(jsonObject);

        assertNotNull(response.responseCode());
        assertThat(response.responseCode(), is(SUCCESS_RESPONSE_CODE));
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);

    }

    private DefaultGoPluginApiRequest buildRequest(String requestName) {
        return new DefaultGoPluginApiRequest(Constants.EXTENSION_TYPE, "1.0", requestName);
    }

    static class ServerSasl {
        public static void main(String[] args) {

            try {
                DirContext ctx = new InitialDirContext();

                Attributes attrs = ctx.getAttributes(
                        "ldap://fmtdc01.corporate.thoughtworks.com:389", new String[]{"supportedSASLMechanisms"});

                System.out.println(attrs);

                ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

}
