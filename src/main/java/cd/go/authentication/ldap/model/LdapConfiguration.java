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
import cd.go.authentication.ldap.utils.Util;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cd.go.authentication.ldap.utils.Util.GSON;

public class LdapConfiguration {
    private static final List<String> DEFAULT_SEARCH_FILTER_ATTRIBUTES = Arrays.asList("sAMAccountName", "uid", "cn", "userPrincipalName", "mail", "otherMailbox");

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
    @ProfileField(key = "ManagerDN", required = true, secure = false)
    private String managerDn;

    @Expose
    @SerializedName("Password")
    @ProfileField(key = "Password", required = true, secure = true)
    private String password;

    @Expose
    @SerializedName("SearchAttributes")
    @ProfileField(key = "SearchAttributes", required = false, secure = false)
    private String searchAttributes;

    @Expose
    @SerializedName("LoginAttribute")
    @ProfileField(key = "LoginAttribute", required = true, secure = false)
    private String loginAttribute;

    @Expose
    @SerializedName("DisplayNameAttribute")
    @ProfileField(key = "DisplayNameAttribute", required = true, secure = false)
    private String displayNameAttribute;

    @Expose
    @SerializedName("EmailAttribute")
    @ProfileField(key = "EmailAttribute", required = true, secure = false)
    private String emailAttribute;

    @Expose
    @SerializedName("LdapConnectionPoolSize")
    private int ldapConnectionPoolSize = 20;

    @Expose
    @SerializedName("LdapConnectionPoolPrefSize")
    private int ldapConnectionPoolPrefSize = 10;

    @Expose
    @SerializedName("LdapConnectionPoolTimeout")
    private long ldapConnectionPoolTimeout = 300000;

    public static LdapConfiguration fromJSON(String json) {
        return GSON.fromJson(json, LdapConfiguration.class);
    }

    public static Map<String, LdapConfiguration> fromJSONMap(String json) {
        JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
        Type type = new TypeToken<Map<String, LdapConfiguration>>() {
        }.getType();
        return GSON.fromJson(jsonObject.get("profiles").toString(), type);
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

    public String getLoginAttribute() {
        return loginAttribute;
    }

    public List<String> getSearchAttributes() {
        final List<String> filters = Util.listFromCommaSeparatedString(this.searchAttributes);
        return filters.isEmpty() ? DEFAULT_SEARCH_FILTER_ATTRIBUTES : filters;
    }

    public String getDisplayNameAttribute() {
        return displayNameAttribute;
    }

    public String getEmailAttribute() {
        return StringUtils.isBlank(emailAttribute) ? "mail" : emailAttribute;
    }

    public UserMapper getUserMapper() {
        return new UserMapper(getLoginAttribute(), getDisplayNameAttribute(), getEmailAttribute());
    }

    public int getLdapConnectionPoolSize() {
        return ldapConnectionPoolSize;
    }

    public int getLdapConnectionPoolPrefSize() {
        return ldapConnectionPoolPrefSize;
    }

    public long getLdapConnectionPoolTimeout() {
        return ldapConnectionPoolTimeout;
    }

    public static List<Map<String, String>> validate(Map<String, String> properties) {
        return MetadataHelper.validate(LdapConfiguration.class, properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LdapConfiguration that = (LdapConfiguration) o;

        if (ldapConnectionPoolSize != that.ldapConnectionPoolSize) return false;
        if (ldapConnectionPoolPrefSize != that.ldapConnectionPoolPrefSize) return false;
        if (ldapConnectionPoolTimeout != that.ldapConnectionPoolTimeout) return false;
        if (ldapUrl != null ? !ldapUrl.equals(that.ldapUrl) : that.ldapUrl != null) return false;
        if (searchBases != null ? !searchBases.equals(that.searchBases) : that.searchBases != null) return false;
        if (managerDn != null ? !managerDn.equals(that.managerDn) : that.managerDn != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (searchAttributes != null ? !searchAttributes.equals(that.searchAttributes) : that.searchAttributes != null)
            return false;
        if (loginAttribute != null ? !loginAttribute.equals(that.loginAttribute) : that.loginAttribute != null)
            return false;
        if (displayNameAttribute != null ? !displayNameAttribute.equals(that.displayNameAttribute) : that.displayNameAttribute != null)
            return false;
        return emailAttribute != null ? emailAttribute.equals(that.emailAttribute) : that.emailAttribute == null;
    }

    @Override
    public int hashCode() {
        int result = ldapUrl != null ? ldapUrl.hashCode() : 0;
        result = 31 * result + (searchBases != null ? searchBases.hashCode() : 0);
        result = 31 * result + (managerDn != null ? managerDn.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (searchAttributes != null ? searchAttributes.hashCode() : 0);
        result = 31 * result + (loginAttribute != null ? loginAttribute.hashCode() : 0);
        result = 31 * result + (displayNameAttribute != null ? displayNameAttribute.hashCode() : 0);
        result = 31 * result + (emailAttribute != null ? emailAttribute.hashCode() : 0);
        result = 31 * result + ldapConnectionPoolSize;
        result = 31 * result + ldapConnectionPoolPrefSize;
        result = 31 * result + (int) (ldapConnectionPoolTimeout ^ (ldapConnectionPoolTimeout >>> 32));
        return result;
    }
}
