package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.model.Capabilities;
import cd.go.authentication.ldap.model.SupportedAuthType;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GetCapabilitiesExecutorTest {

    @Test
    public void shouldAbleSupportPasswordAndSearchCapabilities() throws Exception {
        GoPluginApiResponse response = new GetCapabilitiesExecutor().execute();
        Capabilities capabilities = Capabilities.fromJSON(response.responseBody());

        assertThat(response.responseCode(), CoreMatchers.is(200));
        assertThat(capabilities.getSupportedAuthType(), is(SupportedAuthType.Password));
        assertThat(capabilities.canSearch(), is(true));

        String expectedJSON = "{\n" +
                "    \"supported_auth_type\":\"password\",\n" +
                "    \"can_search\":true\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }

    @Test
    public void shouldAbleSupportWebAndSearchCapabilities() throws Exception {
        GoPluginApiResponse response = new GetCapabilitiesExecutor() {
            @Override
            Capabilities getCapabilities() {
                return new Capabilities(SupportedAuthType.Web, true);
            }
        }.execute();
        Capabilities capabilities = Capabilities.fromJSON(response.responseBody());

        assertThat(response.responseCode(), CoreMatchers.is(200));
        assertThat(capabilities.getSupportedAuthType(), is(SupportedAuthType.Web));
        assertThat(capabilities.canSearch(), is(true));

        String expectedJSON = "{\n" +
                "    \"supported_auth_type\":\"web\",\n" +
                "    \"can_search\":true\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }


}
