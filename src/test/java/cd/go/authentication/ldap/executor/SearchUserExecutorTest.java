package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.RequestBodyMother;
import cd.go.authentication.ldap.exception.ServerRequestFailedException;
import cd.go.authentication.ldap.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchUserExecutorTest {

    private GoPluginApiRequest request;
    private Gson gson = new Gson();

    @Before
    public void setup() throws ServerRequestFailedException {
        request = mock(GoPluginApiRequest.class);
    }

    @Test
    public void shouldAbleToSearchUser() throws Exception {
        when(request.requestBody()).thenReturn(RequestBodyMother.forSearch("bford", "ou=users,ou=system"));

        GoPluginApiResponse response = new SearchUserExecutor(request).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = gson.fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertThat(users, contains(new User("bford", "Bob Ford", "bford@example.com")));
    }

    @Test
    public void shouldAbleToSearchUserFromDifferentSearchBase() throws Exception {
        when(request.requestBody()).thenReturn(RequestBodyMother.forSearch("jdoe", "ou=users,ou=system \n ou=employee,ou=system"));

        GoPluginApiResponse response = new SearchUserExecutor(request).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = gson.fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertThat(users, contains(new User("jdoe", "John Doe", "jdoe@example.com")));
    }

    @Test
    public void shouldReturnEmptySearchResultIfUserNotExist() throws Exception {
        when(request.requestBody()).thenReturn(RequestBodyMother.forSearch("jdoe", "ou=users,ou=system"));

        GoPluginApiResponse response = new SearchUserExecutor(request).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = gson.fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertTrue(users.isEmpty());
    }

    @Test
    public void shouldAbleToSearchUserBasedOnPattern() throws Exception {
        when(request.requestBody()).thenReturn(RequestBodyMother.forSearch("*banks","ou=users,ou=system"));

        GoPluginApiResponse response = new SearchUserExecutor(request).execute();

        assertThat(response.responseCode(), is(200));
        List<User> users = gson.fromJson(response.responseBody(), new TypeToken<List<User>>() {
        }.getType());

        assertThat(users, containsInAnyOrder(
                new User("pbanks", "Phillip Banks", "pbanks@example.com"),
                new User("sbanks", "Sarah Banks", "sbanks@example.com")
        ));
    }


}