package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.model.Metadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.List;

public class GetProfileMetadataExecutor implements RequestExecutor {

    protected static final Metadata LDAP_URL = new Metadata("Url", true, false);
    protected static final Metadata MANAGER_DN = new Metadata("ManagerDN", true, false);
    static final List<Metadata> FIELDS = new ArrayList<>();
    private static final Metadata SEARCH_BASES = new Metadata("SearchBases", true, false);
    private static final Metadata PASSWORD = new Metadata("Password", true, true);
    private static final Metadata SEARCH_FILTER = new Metadata("SearchFilter", true, false);
    private static final Metadata DISPLAY_NAME_ATTRIBUTE = new Metadata("DisplayNameAttribute", true, false);
    private static final Metadata EMAIL_ATTRIBUTE = new Metadata("EmailAttribute", true, false);
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    static {
        FIELDS.add(LDAP_URL);
        FIELDS.add(SEARCH_BASES);
        FIELDS.add(MANAGER_DN);
        FIELDS.add(PASSWORD);
        FIELDS.add(SEARCH_FILTER);
        FIELDS.add(DISPLAY_NAME_ATTRIBUTE);
        FIELDS.add(EMAIL_ATTRIBUTE);
    }

    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
