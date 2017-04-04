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

package cd.go.authentication.ldap;

import cd.go.authentication.ldap.mapper.AttributesMapper;
import cd.go.authentication.ldap.model.*;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;

import javax.naming.directory.Attributes;
import java.util.List;

public class LdapAuthenticator {

    private final LdapFactory ldapFactory;

    public LdapAuthenticator() {
        this(new LdapFactory());
    }

    protected LdapAuthenticator(LdapFactory ldapFactory) {
        this.ldapFactory = ldapFactory;
    }

    public AuthenticationResponse authenticate(Credentials credentials, List<AuthConfig> authConfigs) {
        for (AuthConfig authConfig : authConfigs) {
            AuthenticationResponse authenticationResponse = authenticateWithAuthConfig(credentials, authConfig);
            if (authenticationResponse != null)
                return authenticationResponse;
        }
        return null;
    }

    private AuthenticationResponse authenticateWithAuthConfig(Credentials credentials, AuthConfig authConfig) {
        final LdapConfiguration configuration = authConfig.getConfiguration();
        final String authConfigId = authConfig.getId();
        final Ldap ldap = ldapFactory.ldapForConfiguration(configuration);

        try {
            Attributes attributes = ldap.authenticate(credentials.getUsername(), credentials.getPassword(), new AttributesMapper());
            User user = configuration.getUserMapper().mapFromResult(attributes);
            if (user != null) {
                LdapPlugin.LOG.info("User `" + user.getUsername() + "` successfully authenticated using " + authConfigId);
                return new AuthenticationResponse(user, authConfig);
            }
        } catch (Exception e) {
            LdapPlugin.LOG.error("Failed to authenticate user " + credentials.getUsername() + " on " + configuration.getLdapUrl() + ". ", e);
        }
        return null;
    }
}