package cd.go.authentication.ldap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static cd.go.authentication.ldap.utils.Util.GSON;

public class Authentication {
    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("password")
    private String password;

    public static Authentication fromJSON(String jsonData) {
        return GSON.fromJson(jsonData, Authentication.class);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
