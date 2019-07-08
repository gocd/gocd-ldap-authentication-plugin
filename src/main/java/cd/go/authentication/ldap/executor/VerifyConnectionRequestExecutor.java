/*
 * Copyright 2019 ThoughtWorks, Inc.
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
import cd.go.plugin.base.executors.AbstractExecutor;
import cd.go.plugin.base.validation.DefaultValidator;
import cd.go.plugin.base.validation.ValidationResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.lang3.StringUtils;

import javax.naming.NamingException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.isBlank;
import static cd.go.authentication.ldap.utils.Util.isNotBlank;
import static java.text.MessageFormat.format;

public class VerifyConnectionRequestExecutor extends AbstractExecutor<Map<String, String>> {
    private final Gson GSON = new Gson();
    private final LdapFactory ldapFactory;

    public VerifyConnectionRequestExecutor() {
        this(new LdapFactory());
    }

    protected VerifyConnectionRequestExecutor(LdapFactory ldapFactory) {
        this.ldapFactory = ldapFactory;
    }


    @Override
    protected GoPluginApiResponse execute(Map<String, String> authConfigAsMap) {
        ValidationResult validationResult = new DefaultValidator(LdapConfiguration.class).validate(authConfigAsMap);
        if (isNotBlank(authConfigAsMap.get("ManagerDN")) && isBlank(authConfigAsMap.get("Password"))) {
            validationResult.add("Password", "Password cannot be blank when ManagerDN is provided.");
        }

        if (validationResult.size() != 0) {
            return responseWith("validation-failed", "Validation failed for the given Auth Config", validationResult);
        }

        String error = verifyConnection(LdapConfiguration.fromJSON(GSON.toJson(authConfigAsMap)));
        if (StringUtils.isNotBlank(error)) {
            return responseWith("failure", error, null);
        }

        return successResponse();
    }

    @Override
    protected Map<String, String> parseRequest(String requestBody) {
        return GSON.fromJson(requestBody, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    private String verifyConnection(LdapConfiguration ldapConfiguration) {
        try {
            Ldap ldap = ldapFactory.ldapForConfiguration(ldapConfiguration);
            ldap.validate();
        } catch (NamingException e) {
            LOG.error("[Verify Connection] Verify connection failed with errors.", e);
            return getErrorMessage(e);
        } catch (Exception e) {
            LOG.error("[Verify Connection] Verify connection failed with errors.", e);
            return e.getMessage();
        }

        return null;
    }

    private GoPluginApiResponse successResponse() {
        return responseWith("success", "Connection ok", null);
    }

    private GoPluginApiResponse responseWith(String status, String message, ValidationResult validationResult) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);

        if (validationResult != null && validationResult.size() > 0) {
            response.put("errors", validationResult);
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(response));
    }

    private String getErrorMessage(NamingException e) {
        if (e.getRootCause() != null) {
            final Throwable rootCause = e.getRootCause();
            if (rootCause instanceof UnknownHostException) {
                return format("Unknown host `{0}`.", rootCause.getMessage());
            }
            return rootCause.getMessage();
        }
        return e.getMessage().replaceAll("\\[|\\]", "");
    }
}
