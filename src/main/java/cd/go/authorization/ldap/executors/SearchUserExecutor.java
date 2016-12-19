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
import cd.go.authorization.ldap.models.User;
import cd.go.framework.ldap.LdapSearch;
import cd.go.framework.ldap.filter.LikeFilter;
import cd.go.framework.ldap.filter.OrFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cd.go.authorization.ldap.LdapPlugin.LOG;

public class SearchUserExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static final String SEARCH_TERM = "search_term";
    private static final String SAM_ACCOUNT_NAME = "sAMAccountName";
    private static final String UID = "uid";
    private static final String COMMON_NAME = "cn";
    private static final String USER_PRINCIPLE_NAME = "userPrincipalName";
    private static final String MAIL_ID = "mail";
    private static final String ALIAS_EMAIL_ID = "otherMailbox";

    private final GoPluginApiRequest request;
    private LdapInstances ldapInstances;

    public SearchUserExecutor(GoPluginApiRequest request, LdapInstances ldapInstances) {
        this.request = request;
        this.ldapInstances = ldapInstances;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        Map<String, String> requestParam = GSON.fromJson(request.requestBody(), Map.class);
        String searchTerm = requestParam.get(SEARCH_TERM);

        return new DefaultGoPluginApiResponse(200, GSON.toJson(searchUsers(searchTerm)));
    }

    Set<User> searchUsers(String searchTerm) {
        OrFilter filter = getFilter(searchTerm);
        Set<User> allUsers = new HashSet<>();
        for (String profileId : ldapInstances.getPluginProfile()) {
            try {
                PluginConfiguration pluginConfig = ldapInstances.getPluginConfiguration(profileId);
                LdapSearch ldapSearch = ldapInstances.getLdapSearchInstance(profileId);
                UserMapper userMapper = new UserMapper(pluginConfig.getSearchFilter(), pluginConfig.getDisplayNameAttribute(), "mail");
                List<User> users = ldapSearch.search(filter, userMapper, 100);
                allUsers.addAll(users);
                if (users.size() == 100)
                    break;
            } catch (Exception e) {
                LOG.error("Error while searching user ", e);
            }
        }
        return allUsers;
    }

    private OrFilter getFilter(String searchTerm) {
        OrFilter filter = new OrFilter();
        filter.or(new LikeFilter(SAM_ACCOUNT_NAME, searchTerm));
        filter.or(new LikeFilter(UID, searchTerm));
        filter.or(new LikeFilter(COMMON_NAME, searchTerm));
        filter.or(new LikeFilter(USER_PRINCIPLE_NAME, searchTerm));
        filter.or(new LikeFilter(MAIL_ID, searchTerm));
        filter.or(new LikeFilter(ALIAS_EMAIL_ID, searchTerm));
        return filter;
    }
}
