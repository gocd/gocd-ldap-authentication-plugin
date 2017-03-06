package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.exception.InvalidProfileException;
import cd.go.framework.ldap.Ldap;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;

import javax.naming.NamingException;

public class VerifyConnectionRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final GoPluginApiRequest request;
    private LdapConfiguration ldapConfiguration;

    public VerifyConnectionRequestExecutor(GoPluginApiRequest request) {
        this.request = request;
        ldapConfiguration = LdapConfiguration.fromJSON(request.requestBody());
    }

    @Override
    public GoPluginApiResponse execute() {
        ValidationResult result = new ValidationResult();
        try {
            if (ldapConfiguration == null)
                throw new InvalidProfileException("Profile is empty.");

            Ldap.validate(ldapConfiguration);
        } catch (InvalidProfileException e) {
            result.addError(new ValidationError(e.getMessage()));
        } catch (NamingException e) {
            result.addError(new ValidationError(getErrorMessage(e)));
        } catch (Exception e) {
            result.addError(new ValidationError(e.getMessage()));
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(result.getErrors()));
    }

    private String getErrorMessage(NamingException e) {
        return e.getMessage().replaceAll("\\[|\\]", "");
    }
}
