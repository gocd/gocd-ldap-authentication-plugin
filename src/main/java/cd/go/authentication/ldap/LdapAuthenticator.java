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

package cd.go.authentication.ldap;

import cd.go.authentication.ldap.mapper.LdapMapperFactory;
import cd.go.authentication.ldap.mapper.ResultWrapper;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.*;

import java.util.List;

import static cd.go.authentication.ldap.LdapPlugin.LOG;

public class LdapAuthenticator {
    private final LdapFactory ldapFactory;
    private final LdapMapperFactory ldapMapperFactory;

    public LdapAuthenticator() {
        this(new LdapFactory(), new LdapMapperFactory());
    }

    LdapAuthenticator(LdapFactory ldapFactory, LdapMapperFactory ldapMapperFactory) {
        this.ldapFactory = ldapFactory;
        this.ldapMapperFactory = ldapMapperFactory;
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
        final LdapClient ldap = ldapFactory.ldapForConfiguration(configuration);

        try {
            LOG.debug(String.format("[Authenticate] Authenticating User: %s using auth_config: %s", credentials.getUsername(), authConfigId));
            Object attributesOrEntry = ldap.authenticate(credentials.getUsername(), credentials.getPassword(), ldapMapperFactory.attributeOrEntryMapper());
            User user = configuration.getUserMapper(new UsernameResolver(credentials.getUsername())).mapObject(new ResultWrapper(attributesOrEntry));
            if (user != null) {
                LOG.info(String.format("[Authenticate] User `%s` successfully authenticated using auth_config: %s", user.getUsername(), authConfigId));
                return new AuthenticationResponse(user, authConfig);
            }
        } catch (Exception e) {
            LOG.info("[Authenticate] Failed to authenticate user " + credentials.getUsername() + " on " + configuration.getLdapUrlAsString() + ". ");
            LOG.debug("Exception: ", e);
        }
        return null;
    }
}