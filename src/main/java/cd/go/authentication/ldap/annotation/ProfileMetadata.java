package cd.go.authentication.ldap.annotation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileMetadata implements Metadata {

    @Expose
    @SerializedName("required")
    private boolean required;

    @Expose
    @SerializedName("secure")
    private boolean secure;

    private FieldType type;

    public ProfileMetadata(boolean required, boolean secure, FieldType type) {
        this.required = required;
        this.secure = secure;
        this.type = type;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public FieldType getType() {
        return type;
    }
}
