/*
 * Copyright 2022 Thoughtworks, Inc.
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

import cd.go.authentication.ldap.LdapAuthenticator;
import cd.go.authentication.ldap.model.AuthenticationRequest;
import cd.go.authentication.ldap.model.AuthenticationResponse;
import cd.go.plugin.base.GsonTransformer;
import cd.go.plugin.base.executors.AbstractExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;

public class UserAuthenticationExecutor extends AbstractExecutor<AuthenticationRequest> {
    private final LdapAuthenticator authenticator;

    public UserAuthenticationExecutor() {
        this(new LdapAuthenticator());
    }

    UserAuthenticationExecutor(LdapAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    protected GoPluginApiResponse execute(AuthenticationRequest request) {
        AuthenticationResponse authenticationResponse = authenticator.authenticate(request.getCredentials(), request.getAuthConfigs());

        Map<String, Object> userMap = new HashMap<>();
        if (authenticationResponse != null) {
            userMap.put("user", authenticationResponse.getUser());
            userMap.put("roles", Collections.emptyList());
        }

        return new DefaultGoPluginApiResponse(SUCCESS_RESPONSE_CODE, GsonTransformer.toJson(userMap));
    }

    @Override
    protected AuthenticationRequest parseRequest(String requestBody) {
        return GsonTransformer.fromJson(requestBody, AuthenticationRequest.class);
    }
}
