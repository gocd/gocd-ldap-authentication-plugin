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

package cd.go.authorization.ldap;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Before;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {

    protected GoPluginApiRequest request;
    protected PluginConfiguration pluginConfig;

    @Before
    public void setup() {
        request = mock(GoPluginApiRequest.class);

        pluginConfig = mock(PluginConfiguration.class);
        when(pluginConfig.getLdapUrl()).thenReturn("ldap://localhost:10389");
        when(pluginConfig.getSearchBase()).thenReturn(Arrays.asList(new String[]{"ou=users,ou=system"}));
        when(pluginConfig.getManagerDn()).thenReturn("uid=admin,ou=system");
        when(pluginConfig.getPassword()).thenReturn("secret");
        when(pluginConfig.getSearchFilter()).thenReturn("uid");
        when(pluginConfig.getDisplayNameAttribute()).thenReturn("displayName");
    }
}
