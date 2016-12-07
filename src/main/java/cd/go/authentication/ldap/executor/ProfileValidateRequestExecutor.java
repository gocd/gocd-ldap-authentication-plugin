package cd.go.authentication.ldap.executor;


import cd.go.authentication.ldap.model.Metadata;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.*;

import static cd.go.authentication.ldap.executor.GetProfileMetadataExecutor.FIELDS;


public class ProfileValidateRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final GoPluginApiRequest request;
    private Map<String, String> properties;

    public ProfileValidateRequestExecutor(GoPluginApiRequest request) {
        this.request = request;
        properties = GSON.fromJson(request.requestBody(), Map.class);
    }

    public GoPluginApiResponse execute() throws Exception {
        ArrayList<Map<String, String>> result = new ArrayList<>();

        List<String> knownFields = new ArrayList<>();

        for (Metadata field : FIELDS) {
            knownFields.add(field.getKey());
            Map<String, String> validationError = field.validate(properties.get(field.getKey()));

            if (!validationError.isEmpty()) {
                result.add(validationError);
            }
        }


        Set<String> set = new HashSet<>(properties.keySet());
        set.removeAll(knownFields);

        if (!set.isEmpty()) {
            for (String key : set) {
                LinkedHashMap<String, String> validationError = new LinkedHashMap<>();
                validationError.put("key", key);
                validationError.put("message", "Is an unknown property");
                result.add(validationError);
            }
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(result));
    }
}
