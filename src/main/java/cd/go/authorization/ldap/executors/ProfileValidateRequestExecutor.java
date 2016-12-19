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
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.*;

import static cd.go.authorization.ldap.executors.GetProfileMetadataExecutor.FIELDS;


public class ProfileValidateRequestExecutor implements RequestExecutor {
    private final GoPluginApiRequest request;
    private static final Gson GSON = new Gson();
    private Map<String, String> properties;

    public ProfileValidateRequestExecutor(GoPluginApiRequest request) {
        this.request = request;
        properties = GSON.fromJson(request.requestBody(), Map.class);
    }

    public GoPluginApiResponse execute() throws Exception {
        ArrayList<Map<String, String>> result = new ArrayList<>();

        List<String> knownFields = new ArrayList<>();

        for (Metadata field : FIELDS) {
            knownFields.add(field.getKey());
            Map<String, String> validationError = field.validate(properties.get(field.getKey()));

            if (!validationError.isEmpty()) {
                result.add(validationError);
            }
        }


        Set<String> set = new HashSet<>(properties.keySet());
        set.removeAll(knownFields);

        if (!set.isEmpty()) {
            for (String key : set) {
                LinkedHashMap<String, String> validationError = new LinkedHashMap<>();
                validationError.put("key", key);
                validationError.put("message", "Is an unknown property");
                result.add(validationError);
            }
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(result));
    }
}
