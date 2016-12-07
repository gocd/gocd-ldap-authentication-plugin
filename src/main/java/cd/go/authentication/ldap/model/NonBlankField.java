package cd.go.authentication.ldap.model;

import org.apache.commons.lang3.StringUtils;

public class NonBlankField extends Field {
    public NonBlankField(String key, String displayName, String defaultValue, Boolean secure, String displayOrder) {
        super(key, displayName, defaultValue, true, secure, displayOrder);
    }

    @Override
    public String doValidate(String input) {
        if (StringUtils.isBlank(input)) {
            return this.displayName + " must not be blank.";
        }
        return null;
    }

}
