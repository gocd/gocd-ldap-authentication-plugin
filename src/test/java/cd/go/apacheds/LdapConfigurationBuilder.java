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

package cd.go.apacheds;

import cd.go.authentication.ldap.model.LdapConfiguration;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LdapConfigurationBuilder {
    private final Map<String, Object> configuration = new HashMap<>();

    public LdapConfigurationBuilder() {
        configuration.put("Url", "ldap://localhost:389");
        configuration.put("SearchBases", "ou=system");
        configuration.put("ManagerDN", "uid=admin,ou=system");
        configuration.put("Password", "secret");
        configuration.put("UserLoginFilter", "(uid={0})");
        configuration.put("UserNameAttribute", "uid");
    }

    public LdapConfigurationBuilder withURL(String url) {
        this.configuration.put("Url", url);
        return this;
    }

    public LdapConfigurationBuilder withSearchBases(String... searchBases) {
        this.configuration.put("SearchBases", StringUtils.join(searchBases, "\n"));
        return this;
    }

    public LdapConfigurationBuilder withManagerDN(String managerDN) {
        this.configuration.put("ManagerDN", managerDN);
        return this;
    }

    public LdapConfigurationBuilder withUserLoginFilter(String userLoginFilter) {
        this.configuration.put("UserLoginFilter", userLoginFilter);
        return this;
    }

    public LdapConfigurationBuilder withUserSearchFilter(String userSearchFilter) {
        this.configuration.put("UserSearchFilter", userSearchFilter);
        return this;
    }

    public LdapConfigurationBuilder withPassword(String password) {
        this.configuration.put("Password", password);
        return this;
    }

    public LdapConfigurationBuilder withUserNameAttribute(String userNameAttribute) {
        this.configuration.put("UserNameAttribute", userNameAttribute);
        return this;
    }

    public LdapConfigurationBuilder withSearchTimeout(int searchTimeout) {
        this.configuration.put("SearchTimeout", searchTimeout);
        return this;
    }

    public final LdapConfiguration build() {
        return LdapConfiguration.fromJSON(new Gson().toJson(configuration));
    }
}