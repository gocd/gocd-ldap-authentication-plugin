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

import cd.go.authorization.ldap.LdapInstances;
import cd.go.authorization.ldap.PluginConfiguration;
import cd.go.authorization.ldap.RequestExecutor;
import cd.go.authorization.ldap.UserMapper;
import cd.go.authorization.ldap.models.AuthDetails;
import cd.go.authorization.ldap.models.User;
import cd.go.framework.ldap.LdapSearch;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

import static cd.go.authorization.ldap.LdapPlugin.LOG;
import static com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;

public class UserAuthenticationExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final GoPluginApiRequest request;
    private LdapInstances ldapInstances;

    public UserAuthenticationExecutor(GoPluginApiRequest request, LdapInstances ldapInstances) {
        this.request = request;
        this.ldapInstances = ldapInstances;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        User user = validateAndGetUser();
        Map<String, User> userMap = new HashMap<>();
        userMap.put("user", user);

        DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(SUCCESS_RESPONSE_CODE, GSON.toJson(userMap));
        return response;
    }

    private User validateAndGetUser() {
        AuthDetails authDetails = AuthDetails.getCredential(request.requestBody());

        for (String profileId : ldapInstances.getPluginProfile()) {
            PluginConfiguration pluginConfig = ldapInstances.getPluginConfiguration(profileId);
            try {
                LdapSearch ldapSearch = ldapInstances.getLdapSearchInstance(profileId);
                UserMapper userMapper = new UserMapper(pluginConfig.getSearchFilter(), pluginConfig.getDisplayNameAttribute(), "mail");
                User user = ldapSearch.authenticateUser(authDetails.getUsername(), authDetails.getPassword(), userMapper);

                if (user != null) {
                    LOG.info("User " + user + " successfully authenticated on url " + pluginConfig.getLdapUrl());
                    return user;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("Failed to authenticate user " + authDetails.getUsername() + " on Ldap server " + pluginConfig.getLdapUrl(), e);
            }
        }
        return null;
    }
}
