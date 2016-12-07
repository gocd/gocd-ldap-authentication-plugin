package cd.go.authentication.ldap.executor;

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GetProfileMetadataExecutorTest {

    @Test
    public void shouldSerializeAllFields() throws Exception {
        GoPluginApiResponse response = new GetProfileMetadataExecutor().execute();
        List list = new Gson().fromJson(response.responseBody(), List.class);
        assertEquals(list.size(), GetProfileMetadataExecutor.FIELDS.size());
    }

    @Test
    public void assertJsonStructure() throws Exception {
        GoPluginApiResponse response = new GetProfileMetadataExecutor().execute();

        assertThat(response.responseCode(), is(200));
        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"Url\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"SearchBases\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"ManagerDN\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"Password\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": true\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"SearchFilter\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"DisplayNameAttribute\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"EmailAttribute\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  }\n" +
                "]";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}