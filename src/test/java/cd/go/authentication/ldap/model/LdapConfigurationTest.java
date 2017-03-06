package cd.go.authentication.ldap.model;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LdapConfigurationTest {

    @Test
    public void shouldAbleToDeserializeToLdapProfile() throws Exception {
        String json = "{\n" +
                "  \"ManagerDN\": \"uid=admin,ou=system\",\n" +
                "  \"DisplayNameAttribute\": \"displayName\",\n" +
                "  \"SearchBases\": \"ou=users,ou=system\n" +
                "  ou=employee,ou=system\",\n" +
                "  \"LoginAttribute\": \"uid\",\n" +
                "  \"SearchAttributes\": \"uid,cn\",\n" +
                "  \"Url\": \"ldap://localhost:10389\",\n" +
                "  \"Password\": \"secret\"\n" +
                "}";

        LdapConfiguration ldapConfiguration = LdapConfiguration.fromJSON(json);

        assertNotNull(ldapConfiguration);
        assertThat(ldapConfiguration.getLdapUrl(), is("ldap://localhost:10389"));
        assertThat(ldapConfiguration.getSearchBases(), contains("ou=users,ou=system", "ou=employee,ou=system"));
        assertThat(ldapConfiguration.getManagerDn(), is("uid=admin,ou=system"));
        assertThat(ldapConfiguration.getPassword(), is("secret"));
        assertThat(ldapConfiguration.getLoginAttribute(), is("uid"));
        assertThat(ldapConfiguration.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapConfiguration.getEmailAttribute(), is("mail"));
        assertThat(ldapConfiguration.getSearchAttributes(), contains("uid", "cn"));
    }

    @Test
    public void shouldAbleToDeserializeToLdapProfilesMap() throws Exception {
        String json = "{\n" +
                "  \"profiles\": {\n" +
                "    \"ldap_profile_1\": {\n" +
                "      \"ManagerDN\": \"manger-cred\",\n" +
                "      \"DisplayNameAttribute\": \"displayName\",\n" +
                "      \"SearchBases\": \"base1\",\n" +
                "      \"SearchAttributes\": \"uid\",\n" +
                "      \"LoginAttribute\": \"uid\",\n" +
                "      \"Url\": \"url1\",\n" +
                "      \"Password\": \"secret\"\n" +
                "    },\n" +
                "    \"ldap_profile_2\": {\n" +
                "      \"ManagerDN\": \"manger-cred\",\n" +
                "      \"DisplayNameAttribute\": \"displayName\",\n" +
                "      \"SearchBases\": \"base2\",\n" +
                "      \"LoginAttribute\": \"uid\",\n" +
                "      \"SearchAttributes\": \"uid\",\n" +
                "      \"Url\": \"url2\",\n" +
                "      \"Password\": \"secret\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Map<String, LdapConfiguration> ldapProfileMap = LdapConfiguration.fromJSONMap(json);
        LdapConfiguration ldapConfiguration1 = ldapProfileMap.get("ldap_profile_1");
        LdapConfiguration ldapConfiguration2 = ldapProfileMap.get("ldap_profile_2");

        assertThat(ldapProfileMap.keySet(), contains("ldap_profile_1", "ldap_profile_2"));


        assertThat(ldapConfiguration1.getLdapUrl(), is("url1"));
        assertThat(ldapConfiguration1.getSearchBases(), contains("base1"));
        assertThat(ldapConfiguration1.getManagerDn(), is("manger-cred"));
        assertThat(ldapConfiguration1.getPassword(), is("secret"));
        assertThat(ldapConfiguration1.getLoginAttribute(), is("uid"));
        assertThat(ldapConfiguration1.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapConfiguration1.getEmailAttribute(), is("mail"));

        assertThat(ldapConfiguration2.getLdapUrl(), is("url2"));
        assertThat(ldapConfiguration2.getSearchBases(), contains("base2"));
        assertThat(ldapConfiguration2.getManagerDn(), is("manger-cred"));
        assertThat(ldapConfiguration2.getPassword(), is("secret"));
        assertThat(ldapConfiguration2.getLoginAttribute(), is("uid"));
        assertThat(ldapConfiguration2.getDisplayNameAttribute(), is("displayName"));
        assertThat(ldapConfiguration2.getEmailAttribute(), is("mail"));
    }
}