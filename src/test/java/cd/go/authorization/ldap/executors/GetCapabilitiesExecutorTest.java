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

import cd.go.authorization.ldap.models.Capabilities;
import cd.go.authorization.ldap.models.SupportedAuthType;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GetCapabilitiesExecutorTest {

    @Test
    public void shouldAbleSupportPasswordAndSearchCapabilities() throws Exception {
        GoPluginApiResponse response = new GetCapabilitiesExecutor().execute();
        Capabilities capabilities = Capabilities.fromJSON(response.responseBody());

        assertThat(response.responseCode(), CoreMatchers.is(200));
        assertThat(capabilities.getSupportedAuthType(), is(SupportedAuthType.Password));
        assertThat(capabilities.canSearch(), is(true));

        String expectedJSON = "{\n" +
                "    \"supported_auth_type\":\"password\",\n" +
                "    \"can_search\":true\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldAbleSupportWebAndSearchCapabilities() throws Exception {
        GoPluginApiResponse response = new GetCapabilitiesExecutor() {
            @Override
            Capabilities getCapabilities() {
                return new Capabilities(SupportedAuthType.Web, true);
            }
        }.execute();
        Capabilities capabilities = Capabilities.fromJSON(response.responseBody());

        assertThat(response.responseCode(), CoreMatchers.is(200));
        assertThat(capabilities.getSupportedAuthType(), is(SupportedAuthType.Web));
        assertThat(capabilities.canSearch(), is(true));

        String expectedJSON = "{\n" +
                "    \"supported_auth_type\":\"web\",\n" +
                "    \"can_search\":true\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }


}
