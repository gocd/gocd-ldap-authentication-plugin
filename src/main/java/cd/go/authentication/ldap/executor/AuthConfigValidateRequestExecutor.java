package cd.go.authentication.ldap.executor;


import cd.go.authentication.ldap.model.LdapConfiguration;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.List;
import java.util.Map;


public class AuthConfigValidateRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final GoPluginApiRequest request;
    private Map<String, String> properties;

    public AuthConfigValidateRequestExecutor(GoPluginApiRequest request) {
        this.request = request;
        properties = GSON.fromJson(request.requestBody(), Map.class);
    }

    public GoPluginApiResponse execute() throws Exception {
        final List<Map<String, String>> validationResult = LdapConfiguration.validate(properties);
        return DefaultGoPluginApiResponse.success(GSON.toJson(validationResult));
    }
}
