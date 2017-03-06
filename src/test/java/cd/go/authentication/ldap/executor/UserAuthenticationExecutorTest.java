package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.LdapAuthenticator;
import cd.go.authentication.ldap.RequestBodyMother;
import cd.go.authentication.ldap.model.AuthConfig;
import cd.go.authentication.ldap.model.Credentials;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class UserAuthenticationExecutorTest {
    private GoPluginApiRequest request;
    private LdapAuthenticator ldapAuthenticator;

    @Before
    public void setup() {
        request = mock(GoPluginApiRequest.class);
        ldapAuthenticator = mock(LdapAuthenticator.class);
    }

    @Test
    public void shouldAuthenticateAUser() throws Exception {
        final String requestBody = RequestBodyMother.forAuthenticate("bford", "bob", "ou=users,ou=system");
        final List<AuthConfig> authConfigs = AuthConfig.fromJSONList(requestBody);
        when(request.requestBody()).thenReturn(requestBody);

        new UserAuthenticationExecutor(request, ldapAuthenticator).execute();

        verify(ldapAuthenticator).authenticate(new Credentials("bford", "bob"), authConfigs);
    }
}