/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authorization.ldap.executors;

import cd.go.authorization.ldap.RequestExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.List;

public class GetProfileMetadataExecutor implements RequestExecutor {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static final String KEY_LDAP_URLS = "ldap_url";
    public static final String KEY_SEARCH_BASE = "search_base";
    public static final String KEY_MANAGER_DN = "manager_dn";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SEARCH_FILTER = "search_filter";
    public static final String KEY_DISPLAY_NAME_ATTRIBUTE = "display_name_attribute";

    public static final Metadata LDAP_URLS = new Metadata(KEY_LDAP_URLS, true, false);
    public static final Metadata SEARCH_BASE = new Metadata(KEY_SEARCH_BASE, true, false);
    public static final Metadata MANAGER_DN = new Metadata(KEY_MANAGER_DN, true, false);
    public static final Metadata PASSWORD = new Metadata(KEY_PASSWORD, true, false);
    public static final Metadata SEARCH_FILTER = new Metadata(KEY_SEARCH_FILTER, true, false);
    public static final Metadata DISPLAY_NAME_ATTRIBUTE = new Metadata(KEY_DISPLAY_NAME_ATTRIBUTE, true, false);

    public static final List<Metadata> FIELDS = new ArrayList<>();

    static {
        FIELDS.add(LDAP_URLS);
        FIELDS.add(SEARCH_BASE);
        FIELDS.add(MANAGER_DN);
        FIELDS.add(PASSWORD);
        FIELDS.add(SEARCH_FILTER);
        FIELDS.add(DISPLAY_NAME_ATTRIBUTE);
    }

    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
