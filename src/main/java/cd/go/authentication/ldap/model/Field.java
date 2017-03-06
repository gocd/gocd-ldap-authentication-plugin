package cd.go.authentication.ldap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Field {
    protected final String key;

    @Expose
    @SerializedName("display-name")
    protected String displayName;

    @Expose
    @SerializedName("default-value")
    protected String defaultValue;

    @Expose
    @SerializedName("required")
    protected Boolean required;

    @Expose
    @SerializedName("secure")
    protected Boolean secure;

    @Expose
    @SerializedName("display-order")
    protected String displayOrder;

    public Field(String key, String displayName, String defaultValue, Boolean required, Boolean secure, String displayOrder) {
        this.key = key;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.required = required;
        this.secure = secure;
        this.displayOrder = displayOrder;
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
        return null;
    }

    public String key() {
        return key;
    }


}
