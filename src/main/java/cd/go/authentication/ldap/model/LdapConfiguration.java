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

package cd.go.authentication.ldap.model;

import cd.go.authentication.ldap.annotation.MetadataHelper;
import cd.go.authentication.ldap.annotation.ProfileField;
import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.utils.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cd.go.authentication.ldap.utils.Util.GSON;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LdapConfiguration {
    private static final String DEFAULT_USER_SEARCH_FILTER = "(|(sAMAccountName=*{0}*)(uid=*{0}*)(cn=*{0}*)(mail=*{0}*)(otherMailbox=*{0}*))";

    @Expose
    @SerializedName("Url")
    @ProfileField(key = "Url", required = true, secure = false)
    private String ldapUrl;

    @Expose
    @SerializedName("SearchBases")
    @ProfileField(key = "SearchBases", required = true, secure = false)
    private String searchBases;

    @Expose
    @SerializedName("ManagerDN")
    @ProfileField(key = "ManagerDN", required = false, secure = false)
    private String managerDn;

    @Expose
    @SerializedName("Password")
    @ProfileField(key = "Password", required = false, secure = true)
    private String password;

    @Expose
    @SerializedName("UserSearchFilter")
    @ProfileField(key = "UserSearchFilter", required = false, secure = false)
    private String userSearchFilter;

    @Expose
    @SerializedName("UserLoginFilter")
    @ProfileField(key = "UserLoginFilter", required = true, secure = false)
    private String userLoginFilter;

    @Expose
    @SerializedName("DisplayNameAttribute")
    @ProfileField(key = "DisplayNameAttribute", required = false, secure = false)
    private String displayNameAttribute;

    @Expose
    @SerializedName("EmailAttribute")
    @ProfileField(key = "EmailAttribute", required = false, secure = false)
    private String emailAttribute;

    @Expose
    @SerializedName("LdapConnectionPoolSize")
    private String useConnectionPool = "true";

    public static LdapConfiguration fromJSON(String json) {
        return GSON.fromJson(json, LdapConfiguration.class);
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public List<String> getSearchBases() {
        return Util.splitIntoLinesAndTrimSpaces(searchBases);
    }

    public String getManagerDn() {
        return managerDn;
    }

    public String getPassword() {
        return password;
    }

    public String getUserLoginFilter() {
        return userLoginFilter;
    }

    public String getUserSearchFilter() {
        return isBlank(this.userSearchFilter) ? DEFAULT_USER_SEARCH_FILTER : this.userSearchFilter;
    }

    public String getDisplayNameAttribute() {
        return isBlank(this.displayNameAttribute) ? "cn" : this.displayNameAttribute;
    }

    public String getEmailAttribute() {
        return isBlank(emailAttribute) ? "mail" : emailAttribute;
    }

    public UserMapper getUserMapper(UsernameResolver resolver) {
        return new UserMapper(resolver, getDisplayNameAttribute(), getEmailAttribute());
    }

    public String useConnectionPool() {
        return useConnectionPool;
    }

    public static List<Map<String, String>> validate(Map<String, String> properties) {
        List<Map<String, String>> errors = MetadataHelper.validate(LdapConfiguration.class, properties);

        if (isNotBlank(properties.get("ManagerDN")) && isBlank(properties.get("Password"))) {
            LinkedHashMap<String, String> validationError = new LinkedHashMap<>();
            validationError.put("key", "Password");
            validationError.put("message", "Password cannot be blank when ManagerDN is provided.");
            errors.add(validationError);
        }
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LdapConfiguration that = (LdapConfiguration) o;

        if (ldapUrl != null ? !ldapUrl.equals(that.ldapUrl) : that.ldapUrl != null) return false;
        if (searchBases != null ? !searchBases.equals(that.searchBases) : that.searchBases != null) return false;
        if (managerDn != null ? !managerDn.equals(that.managerDn) : that.managerDn != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (userSearchFilter != null ? !userSearchFilter.equals(that.userSearchFilter) : that.userSearchFilter != null)
            return false;
        if (userLoginFilter != null ? !userLoginFilter.equals(that.userLoginFilter) : that.userLoginFilter != null)
            return false;
        if (displayNameAttribute != null ? !displayNameAttribute.equals(that.displayNameAttribute) : that.displayNameAttribute != null)
            return false;
        if (emailAttribute != null ? !emailAttribute.equals(that.emailAttribute) : that.emailAttribute != null)
            return false;
        return useConnectionPool != null ? useConnectionPool.equals(that.useConnectionPool) : that.useConnectionPool == null;
    }

    @Override
    public int hashCode() {
        int result = ldapUrl != null ? ldapUrl.hashCode() : 0;
        result = 31 * result + (searchBases != null ? searchBases.hashCode() : 0);
        result = 31 * result + (managerDn != null ? managerDn.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (userSearchFilter != null ? userSearchFilter.hashCode() : 0);
        result = 31 * result + (userLoginFilter != null ? userLoginFilter.hashCode() : 0);
        result = 31 * result + (displayNameAttribute != null ? displayNameAttribute.hashCode() : 0);
        result = 31 * result + (emailAttribute != null ? emailAttribute.hashCode() : 0);
        result = 31 * result + (useConnectionPool != null ? useConnectionPool.hashCode() : 0);
        return result;
    }
}
