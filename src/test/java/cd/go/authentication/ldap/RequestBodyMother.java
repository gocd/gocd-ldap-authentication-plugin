package cd.go.authentication.ldap;

import cd.go.authentication.ldap.executor.SearchUserExecutor;
import cd.go.authentication.ldap.model.LdapProfile;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBodyMother {
    private static Gson gson = new Gson();

    public static String forSearch(String searchTerm, String searchBase) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(SearchUserExecutor.SEARCH_TERM, searchTerm);
        requestMap.put("profiles", Collections.singletonMap("ldap", LdapProfile.fromJSON(profileJson(searchBase))));
        return gson.toJson(requestMap);
    }

    public static String forAuthenticate(String username, String password, String searchBase) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("username", username);
        requestMap.put("password", password);
        requestMap.put("profiles", Collections.singletonMap("ldap", LdapProfile.fromJSON(profileJson(searchBase))));
        return gson.toJson(requestMap);
    }

    private static String profileJson(String searchBase) {
        return String.format("{\n" +
                "  \"ManagerDN\": \"uid=admin,ou=system\",\n" +
                "  \"DisplayNameAttribute\": \"displayName\",\n" +
                "  \"SearchBases\": \"%s\",\n" +
                "  \"SearchFilter\": \"uid\",\n" +
                "  \"Url\": \"ldap://localhost:10389\",\n" +
                "  \"Password\": \"secret\"\n" +
                "}", searchBase);
    }
}
