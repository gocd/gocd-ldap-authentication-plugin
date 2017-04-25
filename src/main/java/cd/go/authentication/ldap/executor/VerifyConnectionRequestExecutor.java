/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package cd.go.authentication.ldap.executor;

import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyConnectionRequestExecutor implements RequestExecutor {
        private final Gson GSON = new Gson();
        private final GoPluginApiRequest request;
        private final LdapConfiguration ldapConfiguration;
        private final LdapFactory ldapFactory;

        public VerifyConnectionRequestExecutor(GoPluginApiRequest request) {
            this(request, new LdapFactory());
        }

        protected VerifyConnectionRequestExecutor(GoPluginApiRequest request, LdapFactory ldapFactory) {
            this.ldapFactory = ldapFactory;
            this.request = request;
            this.ldapConfiguration = LdapConfiguration.fromJSON(request.requestBody());
        }

        @Override
        public GoPluginApiResponse execute() {
            List<Map<String, String>> errors = validateAuthConfig();
            if (errors.size() != 0) {
                return validationFailureResponse(errors);
            }

            ValidationResult validationResult = verifyConnection();
            if (!validationResult.isSuccessful()) {
                return verifyConnectionFailureResponse(validationResult);
            }

            return successResponse();
        }

        private ValidationResult verifyConnection() {
            Ldap ldap = ldapFactory.ldapForConfiguration(ldapConfiguration);
            ValidationResult result = new ValidationResult();

            try {
                ldap.validate();
            } catch (NamingException e) {
                result.addError(new ValidationError(getErrorMessage(e)));
            } catch (Exception e) {
                result.addError(new ValidationError(e.getMessage()));
            }
            return result;
        }

        private List<Map<String, String>> validateAuthConfig() {
            Map<String, String> properties = GSON.fromJson(request.requestBody(), new TypeToken<Map<String, String>>() {
            }.getType());
            return LdapConfiguration.validate(properties);
        }

        private GoPluginApiResponse successResponse() {
            return responseWith("success", "Connection ok", null);
        }

        private GoPluginApiResponse verifyConnectionFailureResponse(ValidationResult validationResult) {
            return responseWith("failure", validationResult.getErrors().get(0).getMessage(), null);
        }

        private GoPluginApiResponse validationFailureResponse(List<Map<String, String>> errors) {
            return responseWith("validation-failed", "Validation failed for the given Auth Config", errors);
        }

        private GoPluginApiResponse responseWith(String status, String message, List<Map<String, String>> errors) {
            HashMap<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("message", message);

            if (errors != null && errors.size() > 0) {
                response.put("errors", errors);
            }

            return DefaultGoPluginApiResponse.success(GSON.toJson(response));
        }

        private String getErrorMessage(NamingException e) {
            return e.getMessage().replaceAll("\\[|\\]", "");
        }
}
