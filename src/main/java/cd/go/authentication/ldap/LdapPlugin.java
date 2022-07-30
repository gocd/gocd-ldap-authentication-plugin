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

import cd.go.authentication.ldap.executor.*;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.utils.Util;
import cd.go.authentication.ldap.validators.ManagerCredentialValidator;
import cd.go.plugin.base.dispatcher.BaseBuilder;
import cd.go.plugin.base.dispatcher.RequestDispatcher;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.annotation.Load;
import com.thoughtworks.go.plugin.api.info.PluginContext;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static cd.go.authentication.ldap.Constants.PLUGIN_IDENTIFIER;
import static cd.go.plugin.base.dispatcher.authorization.SupportedAuthType.Password;
import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.INTERNAL_ERROR;

@Extension
public class LdapPlugin implements GoPlugin {
    public static final Logger LOG = Logger.getLoggerFor(LdapPlugin.class);
    private RequestDispatcher requestDispatcher;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        requestDispatcher = BaseBuilder.forAuthorization().v2()
                .icon("/gocd_72_72_icon.png", "image/png")
                .capabilities(Password, true, false, false)
                .authConfigMetadata(LdapConfiguration.class)
                .authConfigView("/auth_config.template.html")
                .verifyConnection(new VerifyConnectionRequestExecutor())
                .validateAuthConfig(new ManagerCredentialValidator())
                .authenticateUser(new UserAuthenticationExecutor())
                .searchUser(new SearchUserExecutor())
                .isValidUser(new IsValidUserRequestExecutor())
                .build();
    }

    @Load
    public void onLoad(PluginContext ctx) {
        LOG.info("Loading plugin " + Util.pluginId() + " version " + Util.pluginVersion());
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        try {
            return requestDispatcher.dispatch(request);
        } catch (NoSuchRequestHandler e) {
            LOG.warn(e.getMessage());
            return new DefaultGoPluginApiResponse(INTERNAL_ERROR, e.getMessage());
        } catch (Exception e) {
            LOG.error("Error while executing request " + request.requestName(), e);
            return new DefaultGoPluginApiResponse(INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return PLUGIN_IDENTIFIER;
    }
}
