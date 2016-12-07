package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.RequestBodyMother;
import cd.go.authentication.ldap.exception.ServerRequestFailedException;
import cd.go.authentication.ldap.model.User;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAuthenticationExecutorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private GoPluginApiRequest request;
    private GoApplicationAccessor accessor;

    @Before
    public void setup() throws ServerRequestFailedException {
        request = mock(GoPluginApiRequest.class);
        accessor = mock(GoApplicationAccessor.class);
        when(accessor.submit(any())).thenReturn(new DefaultGoApiResponse(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE));
    }

    @Test
    public void shouldAbleToAuthenticateUser() throws Exception {
        when(request.requestBody()).thenReturn(RequestBodyMother.forAuthenticate("bford", "bob", "ou=users,ou=system"));

        GoPluginApiResponse response = new UserAuthenticationExecutor(request).execute();

        assertThat(response.responseCode(), is(200));
        User user = getUser(response);
        assertThat(user, is(new User("bford", "Bob Ford", "bford@example.com")));
    }

    @Test
    public void shouldFailedToAuthenticateIfUserIsInDifferentSearchBase() throws Exception {
        String username = "jdoe";
        when(request.requestBody()).thenReturn(RequestBodyMother.forAuthenticate(username, "secret", "ou=users,ou=system"));

        GoPluginApiResponse response = new UserAuthenticationExecutor(request).execute();

        assertThat("{}", is(response.responseBody()));

    }

    @Test
    public void shouldAbleToAuthenticateUserAgainstMultipleSearchBases() throws Exception {
        when(request.requestBody()).thenReturn(RequestBodyMother.forAuthenticate("bford", "bob", "ou=users,ou=system \nou=employee,ou=system"));
        GoPluginApiResponse response = new UserAuthenticationExecutor(request).execute();
        User bob = getUser(response);
        assertThat(bob, is(new User("bford", "Bob Ford", "bford@example.com")));

        when(request.requestBody()).thenReturn(RequestBodyMother.forAuthenticate("jdoe", "john", "ou=users,ou=system \nou=employee,ou=system"));
        response = new UserAuthenticationExecutor(request).execute();
        User jdoe = getUser(response);
        assertThat(jdoe, is(new User("jdoe", "John Doe", "jdoe@example.com")));
    }

    private User getUser(GoPluginApiResponse response) {
        AuthResponse authResponse = new Gson().fromJson(response.responseBody(), AuthResponse.class);
        return authResponse.user;
    }

    private class AuthResponse {
        @SerializedName("user")
        public User user;
    }
}
