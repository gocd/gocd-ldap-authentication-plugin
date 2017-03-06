package cd.go.authentication.ldap;

import cd.go.authentication.ldap.model.LdapConfiguration;
import org.junit.Before;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {
    protected LdapConfiguration ldapConfiguration;
    @Before
    public void setup() {
        ldapConfiguration = mock(LdapConfiguration.class);

        when(ldapConfiguration.getLdapUrl()).thenReturn("ldap://localhost:10389");
        when(ldapConfiguration.getSearchBases()).thenReturn(Arrays.asList("ou=users,ou=system"));
        when(ldapConfiguration.getManagerDn()).thenReturn("uid=admin,ou=system");
        when(ldapConfiguration.getPassword()).thenReturn("secret");
        when(ldapConfiguration.getLoginAttribute()).thenReturn("uid");
        when(ldapConfiguration.getDisplayNameAttribute()).thenReturn("displayName");
        when(ldapConfiguration.getEmailAttribute()).thenReturn("mail");
        when(ldapConfiguration.getUserMapper()).thenCallRealMethod();
    }
}
