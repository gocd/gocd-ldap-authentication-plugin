package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.exception.InvalidProfileException;
import cd.go.authentication.ldap.model.LdapProfile;
import cd.go.framework.ldap.Ldap;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import static cd.go.authentication.ldap.executor.GetProfileMetadataExecutor.LDAP_URL;
import static cd.go.authentication.ldap.executor.GetProfileMetadataExecutor.MANAGER_DN;

public class VerifyConnectionRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final GoPluginApiRequest request;
    private LdapProfile ldapProfile;

    public VerifyConnectionRequestExecutor(GoPluginApiRequest request) {
        this.request = request;
        ldapProfile = LdapProfile.fromJSON(request.requestBody());
    }

    @Override
    public GoPluginApiResponse execute() {
        ValidationResult result = new ValidationResult();
        try {
            if (ldapProfile == null)
                throw new InvalidProfileException("Profile is empty.");

            Ldap.validate(ldapProfile);
        } catch (InvalidProfileException e) {
            result.addError(new ValidationError(e.getMessage()));
        } catch (NamingException e) {
            if (e instanceof AuthenticationException) {
                result.addError(new ValidationError("Failed to authenticate `" + MANAGER_DN.getKey() + "`"));
            } else if (e instanceof CommunicationException) {
                result.addError(new ValidationError(LDAP_URL.getKey(), "Failed to connect " + e.getMessage()));
            } else if (e.getRootCause() instanceof MalformedURLException || e.getRootCause() instanceof UnknownHostException) {
                result.addError(new ValidationError(LDAP_URL.getKey(), e.getMessage()));
            } else if (StringUtils.contains(e.getMessage(), "Incorrect DN")) {
                result.addError(new ValidationError(MANAGER_DN.getKey(), "Manager dn is invalid."));
            } else {
                result.addError(new ValidationError(e.getMessage()));
            }
        } catch (Exception e) {
            result.addError(new ValidationError(e.getMessage()));
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(result.getErrors()));
    }
}
