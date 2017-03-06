package cd.go.authentication.ldap.executor;

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthConfigValidateRequestExecutorTest {

    private GoPluginApiRequest request;

    @Before
    public void setup() throws Exception {
        request = mock(GoPluginApiRequest.class);
    }

    @Test
    public void shouldBarfWhenUnknownKeysArePassed() throws Exception {
        when(request.requestBody()).thenReturn(new Gson().toJson(Collections.singletonMap("foo", "bar")));

        GoPluginApiResponse response = new AuthConfigValidateRequestExecutor(request).execute();
        String json = response.responseBody();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"message\": \"Url must not be blank.\",\n" +
                "    \"key\": \"Url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"SearchBases must not be blank.\",\n" +
                "    \"key\": \"SearchBases\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"ManagerDN must not be blank.\",\n" +
                "    \"key\": \"ManagerDN\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"LoginAttribute must not be blank.\",\n" +
                "    \"key\": \"LoginAttribute\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Password must not be blank.\",\n" +
                "    \"key\": \"Password\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"DisplayNameAttribute must not be blank.\",\n" +
                "    \"key\": \"DisplayNameAttribute\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"EmailAttribute must not be blank.\",\n" +
                "    \"key\": \"EmailAttribute\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"foo\",\n" +
                "    \"message\": \"Is an unknown property\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        when(request.requestBody()).thenReturn(new Gson().toJson(Collections.emptyMap()));

        GoPluginApiResponse response = new AuthConfigValidateRequestExecutor(request).execute();
        String json = response.responseBody();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"message\": \"Url must not be blank.\",\n" +
                "    \"key\": \"Url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"SearchBases must not be blank.\",\n" +
                "    \"key\": \"SearchBases\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"ManagerDN must not be blank.\",\n" +
                "    \"key\": \"ManagerDN\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"LoginAttribute must not be blank.\",\n" +
                "    \"key\": \"LoginAttribute\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Password must not be blank.\",\n" +
                "    \"key\": \"Password\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"DisplayNameAttribute must not be blank.\",\n" +
                "    \"key\": \"DisplayNameAttribute\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"EmailAttribute must not be blank.\",\n" +
                "    \"key\": \"EmailAttribute\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, json, JSONCompareMode.NON_EXTENSIBLE);
    }
}