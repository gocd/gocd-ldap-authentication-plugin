/*
 * Copyright 2019 ThoughtWorks, Inc.
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

import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.GSON;

public class IsValidUserRequestExecutor implements RequestExecutor {
    public static final String USERNAME = "username";
    public static final String AUTH_CONFIG = "auth_config";
    private static final int MAX_SEARCH_RESULT = Integer.MAX_VALUE;

    private final GoPluginApiRequest request;
    private final LdapFactory ldapFactory;

    public IsValidUserRequestExecutor(GoPluginApiRequest request) {
        this(request, new LdapFactory());
    }

    IsValidUserRequestExecutor(GoPluginApiRequest request, LdapFactory ldapFactory) {
        this.request = request;
        this.ldapFactory = ldapFactory;
    }

    @Override
    public GoPluginApiResponse execute() {
        JsonObject jsonObject = GSON.fromJson(request.requestBody(), JsonObject.class);
        Type type = new TypeToken<AuthConfig>() {
        }.getType();

        String usernameToCheck = jsonObject.get(USERNAME).getAsString();
        AuthConfig authConfig = GSON.fromJson(jsonObject.get(AUTH_CONFIG).toString(), type);

        final User found = findUser(usernameToCheck, authConfig);

        if (found != null) {
            return new DefaultGoPluginApiResponse(200);
        }

        return new DefaultGoPluginApiResponse(404);
    }

    private User findUser(String usernameToCheck, AuthConfig authConfig) {
        try {
            final LdapConfiguration configuration = authConfig.getConfiguration();
            final Ldap ldap = ldapFactory.ldapForConfiguration(configuration);
            String userSearchFilter = configuration.getUserSearchFilter();

            LOG.debug(String.format("[Is User Valid] Looking up for user with name: `%s`" +
                    " using the search_filter: `%s` and auth_config: `%s`", usernameToCheck, userSearchFilter, authConfig.getId()));

            final List<User> users = ldap.search(userSearchFilter, new String[]{usernameToCheck}, configuration.getUserMapper(new UsernameResolver()), MAX_SEARCH_RESULT);
            return users.stream().filter(user -> user.getUsername().equalsIgnoreCase(usernameToCheck)).collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            LOG.error(String.format("[Is User Valid] Failed to find user with name `%s` using auth_config: `%s`", usernameToCheck, authConfig.getId()), e);
        }

        return null;
    }
}
