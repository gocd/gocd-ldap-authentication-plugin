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
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GetProfileMetadataExecutorTest {

    @Test
    public void shouldSerializeAllFields() throws Exception {
        GoPluginApiResponse response = new GetProfileMetadataExecutor().execute();
        List list = new Gson().fromJson(response.responseBody(), List.class);
        assertEquals(list.size(), GetProfileMetadataExecutor.FIELDS.size());
    }

    @Test
    public void assertJsonStructure() throws Exception {
        GoPluginApiResponse response = new GetProfileMetadataExecutor().execute();

        assertThat(response.responseCode(), is(200));
        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"ldap_url\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"search_base\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"manager_dn\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"password\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"search_filter\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"display_name_attribute\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  }\n" +
                "]";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}