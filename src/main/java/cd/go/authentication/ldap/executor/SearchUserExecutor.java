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

package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.GSON;

public class SearchUserExecutor implements RequestExecutor {
    public static final String SEARCH_TERM = "search_term";
    private static final int MAX_SEARCH_RESULT = 100;

    private final GoPluginApiRequest request;
    private final LdapFactory ldapFactory;

    public SearchUserExecutor(GoPluginApiRequest request) {
        this(request, new LdapFactory());
    }

    SearchUserExecutor(GoPluginApiRequest request, LdapFactory ldapFactory) {
        this.request = request;
        this.ldapFactory = ldapFactory;
    }

    @Override
    public GoPluginApiResponse execute() {
        Map<String, String> requestParam = GSON.fromJson(request.requestBody(), Map.class);
        String searchTerm = requestParam.get(SEARCH_TERM);
        List<AuthConfig> authConfigs = AuthConfig.fromJSONList(request.requestBody());

        final Set<User> users = searchUsers(searchTerm, authConfigs);

        return new DefaultGoPluginApiResponse(200, GSON.toJson(users));
    }

    Set<User> searchUsers(String searchTerm, List<AuthConfig> authConfigs) {
        final Set<User> allUsers = new HashSet<>();
        for (AuthConfig authConfig : authConfigs) {
            final int remainingResultCount = MAX_SEARCH_RESULT - allUsers.size();
            try {
                final LdapConfiguration configuration = authConfig.getConfiguration();
                final Ldap ldap = ldapFactory.ldapForConfiguration(configuration);
                String userSearchFilter = configuration.getUserSearchFilter();

                LOG.info(String.format("[User Search] Looking up for users matching search_term: `%s`" +
                        " using the search_filter: `%s` and auth_config: `%s`", searchTerm, userSearchFilter, authConfig.getId()));

                final List<User> users = ldap.search(userSearchFilter, new String[]{searchTerm}, configuration.getUserMapper(new UsernameResolver()), remainingResultCount);
                allUsers.addAll(users);

                if (allUsers.size() >= MAX_SEARCH_RESULT) {
                    break;
                }

            } catch (Exception e) {
                LOG.error(String.format("[User Search] Failed to search user using auth_config: `%s`", authConfig.getId()), e);
            }
        }
        return allUsers;
    }
}
