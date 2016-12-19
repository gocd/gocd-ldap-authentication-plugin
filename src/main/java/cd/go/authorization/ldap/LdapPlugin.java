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

import cd.go.authorization.ldap.executors.*;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static cd.go.authorization.ldap.Constants.API_VERSION;
import static cd.go.authorization.ldap.Constants.PLUGIN_IDENTIFIER;

@Extension
public class LdapPlugin implements GoPlugin {

    public static final Logger LOG = Logger.getLoggerFor(LdapPlugin.class);

    private GoApplicationAccessor accessor;
    private PluginRequest pluginRequest;
    private LdapInstances ldapInstances;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        this.accessor = accessor;
        this.pluginRequest = new PluginRequest(accessor);
        this.ldapInstances = new LdapInstances();
        getPluginConfigurationFromServer();
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        try {
            switch (Request.fromString(request.requestName())) {
                case REQUEST_GET_PLUGIN_ICON:
                    return new GetPluginIconExecutor().execute();
                case REQUEST_GET_CAPABILITIES:
                    return new GetCapabilitiesExecutor().execute();
                case REQUEST_GET_PLUGIN_CONFIG_METADATA:
                    return new GetProfileMetadataExecutor().execute();
                case REQUEST_PLUGIN_CONFIG_VIEW:
                    return new GetProfileViewExecutor().execute();
                case REQUEST_VALIDATE_PLUGIN_CONFIG:
                    return new ProfileValidateRequestExecutor(request).execute();
                case REQUEST_VERIFY_CONNECTION:
                    return null;
                case REQUEST_AUTHENTICATE_USER:
                    return new UserAuthenticationExecutor(request, ldapInstances).execute();
                case REQUEST_SEARCH_USERS:
                    return new SearchUserExecutor(request, ldapInstances).execute();
                default:
                    throw new UnhandledRequestTypeException(request.requestName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return PLUGIN_IDENTIFIER;
    }


    public void getPluginConfigurationFromServer() {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Request.REQUEST_PLUGIN_CONFIGURATION_FROM_SERVER.requestName(), API_VERSION, PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);
        ldapInstances.createLdapSearchInstances(PluginConfiguration.fromJSONMap(response.responseBody()));
    }

}
