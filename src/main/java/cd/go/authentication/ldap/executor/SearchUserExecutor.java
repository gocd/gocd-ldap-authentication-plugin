package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.LdapPlugin;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import cd.go.framework.ldap.filter.LikeFilter;
import cd.go.framework.ldap.filter.OrFilter;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cd.go.authentication.ldap.utils.Util.GSON;

public class SearchUserExecutor implements RequestExecutor {
    public static final String SEARCH_TERM = "search_term";

    private final GoPluginApiRequest request;
    private final LdapFactory ldapFactory;

    public SearchUserExecutor(GoPluginApiRequest request) {
        this(request, new LdapFactory());
    }

    protected SearchUserExecutor(GoPluginApiRequest request, LdapFactory ldapFactory) {
        this.request = request;
        this.ldapFactory = ldapFactory;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        Map<String, String> requestParam = GSON.fromJson(request.requestBody(), Map.class);
        String searchTerm = requestParam.get(SEARCH_TERM);
        List<AuthConfig> authConfigs = AuthConfig.fromJSONList(request.requestBody());

        final Set<User> users = searchUsers(searchTerm, authConfigs);

        return new DefaultGoPluginApiResponse(200, GSON.toJson(users));
    }

    Set<User> searchUsers(String searchTerm, List<AuthConfig> authConfigs) {
        Set<User> allUsers = new HashSet<>();
        for (AuthConfig authConfig : authConfigs) {
            try {
                final LdapConfiguration configuration = authConfig.getConfiguration();
                final Ldap ldap = ldapFactory.ldapForConfiguration(configuration);
                OrFilter filter = getFilter(searchTerm, configuration.getSearchAttributes());

                List<User> users = ldap.search(filter, configuration.getUserMapper(), 100);
                allUsers.addAll(users);
                if (users.size() == 100)
                    break;
            } catch (Exception e) {
                LdapPlugin.LOG.error("Failed to search user using ldap profile `" + authConfig.getId() + "` ", e);
            }
        }
        return allUsers;
    }

    private OrFilter getFilter(String searchTerm, List<String> filterAttributes) {
        OrFilter filter = new OrFilter();
        for (String attribute : filterAttributes) {
            filter.or(new LikeFilter(attribute, searchTerm));
        }

        return filter;
    }
}
