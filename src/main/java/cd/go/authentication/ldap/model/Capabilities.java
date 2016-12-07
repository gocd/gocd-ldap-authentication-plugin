package cd.go.authentication.ldap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static cd.go.authentication.ldap.utils.Util.GSON;

public class Capabilities {
    @Expose
    @SerializedName("supported_auth_type")
    private final SupportedAuthType supportedAuthType;

    @Expose
    @SerializedName("can_search")
    private final boolean canSearch;

    public Capabilities(SupportedAuthType supportedAuthType, boolean canSearch) {
        this.supportedAuthType = supportedAuthType;
        this.canSearch = canSearch;
    }

    public static Capabilities fromJSON(String json) {
        return GSON.fromJson(json, Capabilities.class);
    }

    public SupportedAuthType getSupportedAuthType() {
        return supportedAuthType;
    }

    public boolean canSearch() {
        return canSearch;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }
}
