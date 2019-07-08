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

package cd.go.authentication.ldap.model;

import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.utils.Util;
import cd.go.plugin.base.annotations.Property;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.url.LdapUrl;

import java.util.List;
import java.util.Objects;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.GSON;
import static cd.go.authentication.ldap.utils.Util.isBlank;

public class LdapConfiguration {
    private static final String DEFAULT_USER_SEARCH_FILTER = "(|(sAMAccountName=*{0}*)(uid=*{0}*)(cn=*{0}*)(mail=*{0}*)(otherMailbox=*{0}*))";

    @Expose
    @SerializedName("Url")
    @Property(name = "Url", required = true, secure = false)
    private String ldapUrl;

    @Expose
    @SerializedName("SearchBases")
    @Property(name = "SearchBases", required = true, secure = false)
    private String searchBases;

    @Expose
    @SerializedName("ManagerDN")
    @Property(name = "ManagerDN", required = false, secure = false)
    private String managerDn;

    @Expose
    @SerializedName("Password")
    @Property(name = "Password", required = false, secure = true)
    private String password;

    @Expose
    @SerializedName("UserSearchFilter")
    @Property(name = "UserSearchFilter", required = false, secure = false)
    private String userSearchFilter;

    @Expose
    @SerializedName("UserLoginFilter")
    @Property(name = "UserLoginFilter", required = true, secure = false)
    private String userLoginFilter;

    @Expose
    @SerializedName("DisplayNameAttribute")
    @Property(name = "DisplayNameAttribute", required = false, secure = false)
    private String displayNameAttribute;

    @Expose
    @SerializedName("EmailAttribute")
    @Property(name = "EmailAttribute", required = false, secure = false)
    private String emailAttribute;

    @Expose
    @SerializedName("SearchTimeout")
    @Property(name = "SearchTimeout", required = false, secure = false)
    private String searchTimeout = "5";

    public static LdapConfiguration fromJSON(String json) {
        return GSON.fromJson(json, LdapConfiguration.class);
    }

    public String getLdapUrlAsString() {
        return ldapUrl;
    }

    public LdapUrl getLdapUrl() {
        try {
            return new LdapUrl(ldapUrl);
        } catch (Exception e) {
            LOG.error("Error while parsing url", e);
        }
        return null;
    }

    public boolean useSSL() {
        return LdapUrl.LDAPS_SCHEME.equalsIgnoreCase(getLdapUrl().getScheme());
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

    public int getSearchTimeout() {
        final String timeout = StringUtils.stripToEmpty(searchTimeout);
        if (StringUtils.isBlank(timeout)) {
            return 5;
        }
        return Integer.parseInt(timeout);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapConfiguration that = (LdapConfiguration) o;
        return Objects.equals(ldapUrl, that.ldapUrl) &&
                Objects.equals(searchBases, that.searchBases) &&
                Objects.equals(managerDn, that.managerDn) &&
                Objects.equals(password, that.password) &&
                Objects.equals(userSearchFilter, that.userSearchFilter) &&
                Objects.equals(userLoginFilter, that.userLoginFilter) &&
                Objects.equals(displayNameAttribute, that.displayNameAttribute) &&
                Objects.equals(emailAttribute, that.emailAttribute) &&
                Objects.equals(searchTimeout, that.searchTimeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ldapUrl, searchBases, managerDn, password, userSearchFilter, userLoginFilter, displayNameAttribute, emailAttribute, searchTimeout);
    }
}
