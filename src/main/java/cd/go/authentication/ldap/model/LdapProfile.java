package cd.go.authentication.ldap.model;

import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.utils.Util;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static cd.go.authentication.ldap.utils.Util.GSON;

public class LdapProfile {
    @Expose
    @SerializedName("Url")
    private String ldapUrl;
    @Expose
    @SerializedName("SearchBases")
    private String searchBases;
    @Expose
    @SerializedName("ManagerDN")
    private String managerDn;
    @Expose
    @SerializedName("Password")
    private String password;
    @Expose
    @SerializedName("SearchFilter")
    private String searchFilter;
    @Expose
    @SerializedName("DisplayNameAttribute")
    private String displayNameAttribute;
    @Expose
    @SerializedName("EmailAttribute")
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

    public static LdapProfile fromJSON(String json) {
        return GSON.fromJson(json, LdapProfile.class);
    }

    public static Map<String, LdapProfile> fromJSONMap(String json) {
        JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
        Type type = new TypeToken<Map<String, LdapProfile>>() {
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

    public String getSearchFilter() {
        return searchFilter;
    }

    public String getDisplayNameAttribute() {
        return displayNameAttribute;
    }

    public String getEmailAttribute() {
        return StringUtils.isBlank(emailAttribute) ? "mail" : emailAttribute;
    }

    public UserMapper getUserMapper() {
        return new UserMapper(getSearchFilter(), getDisplayNameAttribute(), getEmailAttribute());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LdapProfile)) return false;

        LdapProfile that = (LdapProfile) o;

        if (ldapConnectionPoolSize != that.ldapConnectionPoolSize) return false;
        if (ldapConnectionPoolPrefSize != that.ldapConnectionPoolPrefSize) return false;
        if (ldapConnectionPoolTimeout != that.ldapConnectionPoolTimeout) return false;
        if (ldapUrl != null ? !ldapUrl.equals(that.ldapUrl) : that.ldapUrl != null) return false;
        if (searchBases != null ? !searchBases.equals(that.searchBases) : that.searchBases != null) return false;
        if (managerDn != null ? !managerDn.equals(that.managerDn) : that.managerDn != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (searchFilter != null ? !searchFilter.equals(that.searchFilter) : that.searchFilter != null) return false;
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
        result = 31 * result + (searchFilter != null ? searchFilter.hashCode() : 0);
        result = 31 * result + (displayNameAttribute != null ? displayNameAttribute.hashCode() : 0);
        result = 31 * result + (emailAttribute != null ? emailAttribute.hashCode() : 0);
        result = 31 * result + ldapConnectionPoolSize;
        result = 31 * result + ldapConnectionPoolPrefSize;
        result = 31 * result + (int) (ldapConnectionPoolTimeout ^ (ldapConnectionPoolTimeout >>> 32));
        return result;
    }
}
