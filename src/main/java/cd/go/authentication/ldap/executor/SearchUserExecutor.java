package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.model.LdapProfile;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.filter.LikeFilter;
import cd.go.framework.ldap.filter.OrFilter;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cd.go.authentication.ldap.LdapAuthenticationPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.GSON;

public class SearchUserExecutor implements RequestExecutor {
    public static final String SEARCH_TERM = "search_term";
    private static final String SAM_ACCOUNT_NAME = "sAMAccountName";
    private static final String UID = "uid";
    private static final String COMMON_NAME = "cn";
    private static final String USER_PRINCIPLE_NAME = "userPrincipalName";
    private static final String MAIL_ID = "mail";
    private static final String ALIAS_EMAIL_ID = "otherMailbox";

    private final GoPluginApiRequest request;

    public SearchUserExecutor(GoPluginApiRequest request) {
        this.request = request;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        Map<String, String> requestParam = GSON.fromJson(request.requestBody(), Map.class);
        String searchTerm = requestParam.get(SEARCH_TERM);
        Map<String, LdapProfile> ldapProfiles = LdapProfile.fromJSONMap(request.requestBody());

        return new DefaultGoPluginApiResponse(200, GSON.toJson(searchUsers(searchTerm, ldapProfiles)));
    }

    Set<User> searchUsers(String searchTerm, Map<String, LdapProfile> ldapProfiles) {
        OrFilter filter = getFilter(searchTerm);
        Set<User> allUsers = new HashSet<>();
        for (Map.Entry<String, LdapProfile> entry : ldapProfiles.entrySet()) {
            String profileId = entry.getKey();
            LdapProfile profile = entry.getValue();

            try {
                List<User> users = new Ldap(profile).search(filter, profile.getUserMapper(), 100);
                allUsers.addAll(users);
                if (users.size() == 100)
                    break;
            } catch (Exception e) {
                LOG.error("Failed to search user using ldap profile `" + profileId + "` ", e);
            }
        }
        return allUsers;
    }

    private OrFilter getFilter(String searchTerm) {
        OrFilter filter = new OrFilter();
        filter.or(new LikeFilter(SAM_ACCOUNT_NAME, searchTerm));
        filter.or(new LikeFilter(UID, searchTerm));
        filter.or(new LikeFilter(COMMON_NAME, searchTerm));
        filter.or(new LikeFilter(USER_PRINCIPLE_NAME, searchTerm));
        filter.or(new LikeFilter(MAIL_ID, searchTerm));
        filter.or(new LikeFilter(ALIAS_EMAIL_ID, searchTerm));
        return filter;
    }
}
