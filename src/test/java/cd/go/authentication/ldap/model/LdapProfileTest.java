package cd.go.authentication.ldap.model;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LdapProfileTest {

    @Test
    public void shouldAbleToDeserializeToLdapProfile() throws Exception {
        String json = "{\n" +
                "  \"ManagerDN\": \"uid=admin,ou=system\",\n" +
                "  \"DisplayNameAttribute\": \"displayName\",\n" +
                "  \"SearchBases\": \"ou=users,ou=system\n" +
                "  ou=employee,ou=system\",\n" +
                "  \"SearchFilter\": \"uid\",\n" +
                "  \"Url\": \"ldap://localhost:10389\",\n" +
                "  \"Password\": \"secret\"\n" +
                "}";

        LdapProfile ldapProfile = LdapProfile.fromJSON(json);

        assertNotNull(ldapProfile);
        assertThat(ldapProfile.getLdapUrl(), is("ldap://localhost:10389"));
        assertThat(ldapProfile.getSearchBases(), contains("ou=users,ou=system", "ou=employee,ou=system"));
        assertThat(ldapProfile.getManagerDn(), is("uid=admin,ou=system"));
        assertThat(ldapProfile.getPassword(), is("secret"));
        assertThat(ldapProfile.getSearchFilter(), is("uid"));
        assertThat(ldapProfile.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapProfile.getEmailAttribute(), is("mail"));
    }

    @Test
    public void shouldAbleToDeserializeToLdapProfilesMap() throws Exception {
        String json = "{\n" +
                "  \"profiles\": {\n" +
                "    \"ldap_profile_1\": {\n" +
                "      \"ManagerDN\": \"manger-cred\",\n" +
                "      \"DisplayNameAttribute\": \"displayName\",\n" +
                "      \"SearchBases\": \"base1\",\n" +
                "      \"SearchFilter\": \"uid\",\n" +
                "      \"Url\": \"url1\",\n" +
                "      \"Password\": \"secret\"\n" +
                "    },\n" +
                "    \"ldap_profile_2\": {\n" +
                "      \"ManagerDN\": \"manger-cred\",\n" +
                "      \"DisplayNameAttribute\": \"displayName\",\n" +
                "      \"SearchBases\": \"base2\",\n" +
                "      \"SearchFilter\": \"uid\",\n" +
                "      \"Url\": \"url2\",\n" +
                "      \"Password\": \"secret\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Map<String, LdapProfile> ldapProfileMap = LdapProfile.fromJSONMap(json);
        LdapProfile ldapProfile1 = ldapProfileMap.get("ldap_profile_1");
        LdapProfile ldapProfile2 = ldapProfileMap.get("ldap_profile_2");

        assertThat(ldapProfileMap.keySet(), contains("ldap_profile_1", "ldap_profile_2"));


        assertThat(ldapProfile1.getLdapUrl(), is("url1"));
        assertThat(ldapProfile1.getSearchBases(), contains("base1"));
        assertThat(ldapProfile1.getManagerDn(), is("manger-cred"));
        assertThat(ldapProfile1.getPassword(), is("secret"));
        assertThat(ldapProfile1.getSearchFilter(), is("uid"));
        assertThat(ldapProfile1.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapProfile1.getEmailAttribute(), is("mail"));

        assertThat(ldapProfile2.getLdapUrl(), is("url2"));
        assertThat(ldapProfile2.getSearchBases(), contains("base2"));
        assertThat(ldapProfile2.getManagerDn(), is("manger-cred"));
        assertThat(ldapProfile2.getPassword(), is("secret"));
        assertThat(ldapProfile2.getSearchFilter(), is("uid"));
        assertThat(ldapProfile2.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapProfile2.getEmailAttribute(), is("mail"));
    }
}