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

import cd.go.authorization.ldap.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class PluginConfiguration {
    private static final Gson GSON = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation().
            create();
    @Expose
    @SerializedName("ldap_url")
    private String ldapUrl;
    @Expose
    @SerializedName("search_base")
    private String searchBase;
    @Expose
    @SerializedName("manager_dn")
    private String managerDn;
    @Expose
    @SerializedName("password")
    private String password;
    @Expose
    @SerializedName("search_filter")
    private String searchFilter;
    @Expose
    @SerializedName("display_name_attribute")
    private String displayNameAttribute;

    public static PluginConfiguration fromJSON(String json) {
        return GSON.fromJson(json, PluginConfiguration.class);
    }

    public static Map<String, PluginConfiguration> fromJSONMap(String json) {
        Type type = new TypeToken<Map<String, PluginConfiguration>>() {
        }.getType();
        return GSON.fromJson(json, type);
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public List<String> getSearchBase() {
        return Util.splitIntoLinesAndTrimSpaces(searchBase);
    }

    public String getManagerDn() {
        return managerDn;
    }

    public String getPassword() {
        return password;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public String getDisplayNameAttribute() {
        return displayNameAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginConfiguration that = (PluginConfiguration) o;

        if (ldapUrl != null ? !ldapUrl.equals(that.ldapUrl) : that.ldapUrl != null) return false;
        if (searchBase != null ? !searchBase.equals(that.searchBase) : that.searchBase != null) return false;
        if (managerDn != null ? !managerDn.equals(that.managerDn) : that.managerDn != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (searchFilter != null ? !searchFilter.equals(that.searchFilter) : that.searchFilter != null) return false;
        return displayNameAttribute != null ? displayNameAttribute.equals(that.displayNameAttribute) : that.displayNameAttribute == null;
    }

    @Override
    public int hashCode() {
        int result = ldapUrl != null ? ldapUrl.hashCode() : 0;
        result = 31 * result + (searchBase != null ? searchBase.hashCode() : 0);
        result = 31 * result + (managerDn != null ? managerDn.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (searchFilter != null ? searchFilter.hashCode() : 0);
        result = 31 * result + (displayNameAttribute != null ? displayNameAttribute.hashCode() : 0);
        return result;
    }
}
