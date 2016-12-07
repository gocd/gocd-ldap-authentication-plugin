package cd.go.authentication.ldap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    @Expose
    @SerializedName("key")
    private String key;

    @Expose
    @SerializedName("metadata")
    private ProfileMetadata metadata;

    public Metadata(String key, boolean required, boolean secure) {
        this(key, new ProfileMetadata(required, secure));
    }

    public Metadata(String key) {
        this(key, new ProfileMetadata(false, false));
    }

    public Metadata(String key, ProfileMetadata metadata) {
        this.key = key;
        this.metadata = metadata;
    }

    public Map<String, String> validate(String input) {
        HashMap<String, String> result = new HashMap<>();
        String validationError = doValidate(input);
        if (StringUtils.isNotBlank(validationError)) {
            result.put("key", key);
            result.put("message", validationError);
        }
        return result;
    }

    protected String doValidate(String input) {
        if (isRequired()) {
            if (StringUtils.isBlank(input)) {
                return this.key + " must not be blank.";
            }
        }
        return null;
    }


    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return metadata.required;
    }

    public static class ProfileMetadata {
        @Expose
        @SerializedName("required")
        private Boolean required;

        @Expose
        @SerializedName("secure")
        private Boolean secure;

        public ProfileMetadata(boolean required, boolean secure) {
            this.required = required;
            this.secure = secure;
        }
    }
}