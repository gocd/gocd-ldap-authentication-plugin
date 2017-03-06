package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.annotation.MetadataHelper;
import cd.go.authentication.ldap.model.LdapConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class GetAuthConfigMetadataExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(MetadataHelper.getMetadata(LdapConfiguration.class)));
    }
}
