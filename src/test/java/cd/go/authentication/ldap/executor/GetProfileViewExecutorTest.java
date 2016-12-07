package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.model.Metadata;
import cd.go.authentication.ldap.utils.Util;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GetProfileViewExecutorTest {

    @Test
    public void shouldRenderTheTemplateInJSON() throws Exception {
        GoPluginApiResponse response = new GetProfileViewExecutor().execute();
        assertThat(response.responseCode(), is(200));
        Map<String, String> hashSet = new Gson().fromJson(response.responseBody(), HashMap.class);
        assertThat(hashSet, hasEntry("template", Util.readResource("/profile.template.html")));
    }

    @Test
    public void allFieldsShouldBePresentInView() throws Exception {
        String template = Util.readResource("/profile.template.html");

        for (Metadata field : GetProfileMetadataExecutor.FIELDS) {
            assertThat(template, containsString("ng-model=\"" + field.getKey() + "\""));
            assertThat(template, containsString("<span class=\"form_error form-error\" ng-class=\"{'is-visible': GOINPUTNAME[" +
                    field.getKey() + "].$error.server}\" ng-show=\"GOINPUTNAME[" +
                    field.getKey() + "].$error.server\">{{GOINPUTNAME[" +
                    field.getKey() + "].$error.server}}</span>"));
        }
    }
}