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

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfileValidateRequestExecutorTest {

    private GoPluginApiRequest request;

    @Before
    public void setup() throws Exception {
        request = mock(GoPluginApiRequest.class);
    }

    @Test
    public void shouldBarfWhenUnknownKeysArePassed() throws Exception {
        when(request.requestBody()).thenReturn(new Gson().toJson(Collections.singletonMap("foo", "bar")));

        GoPluginApiResponse response = new ProfileValidateRequestExecutor(request).execute();
        String json = response.responseBody();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"message\": \"ldap_url must not be blank.\",\n" +
                "    \"key\": \"ldap_url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"search_base must not be blank.\",\n" +
                "    \"key\": \"search_base\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"manager_dn must not be blank.\",\n" +
                "    \"key\": \"manager_dn\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"password must not be blank.\",\n" +
                "    \"key\": \"password\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"search_filter must not be blank.\",\n" +
                "    \"key\": \"search_filter\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"display_name_attribute must not be blank.\",\n" +
                "    \"key\": \"display_name_attribute\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"foo\",\n" +
                "    \"message\": \"Is an unknown property\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        when(request.requestBody()).thenReturn(new Gson().toJson(Collections.emptyMap()));

        GoPluginApiResponse response = new ProfileValidateRequestExecutor(request).execute();
        String json = response.responseBody();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"message\": \"ldap_url must not be blank.\",\n" +
                "    \"key\": \"ldap_url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"search_base must not be blank.\",\n" +
                "    \"key\": \"search_base\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"manager_dn must not be blank.\",\n" +
                "    \"key\": \"manager_dn\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"password must not be blank.\",\n" +
                "    \"key\": \"password\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"search_filter must not be blank.\",\n" +
                "    \"key\": \"search_filter\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"display_name_attribute must not be blank.\",\n" +
                "    \"key\": \"display_name_attribute\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, json, JSONCompareMode.NON_EXTENSIBLE);
    }
}