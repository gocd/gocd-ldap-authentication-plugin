package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.LdapAuthenticator;
import cd.go.authentication.ldap.model.Authentication;
import cd.go.authentication.ldap.model.AuthenticationResponse;
import cd.go.authentication.ldap.model.LdapProfile;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;

public class UserAuthenticationExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final GoPluginApiRequest request;

    public UserAuthenticationExecutor(GoPluginApiRequest request) {
        this.request = request;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        Authentication authentication = Authentication.fromJSON(request.requestBody());
        Map<String, LdapProfile> ldapProfileMap = LdapProfile.fromJSONMap(request.requestBody());

        LdapAuthenticator ldapAuthenticator = new LdapAuthenticator();
        AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(authentication, ldapProfileMap);

        Map<String, Object> userMap = new HashMap<>();
        if (authenticationResponse != null) {
            userMap.put("user", authenticationResponse.getUser());
            userMap.put("roles", Collections.emptyList());
        }

        DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(SUCCESS_RESPONSE_CODE, GSON.toJson(userMap));
        return response;
    }
}
