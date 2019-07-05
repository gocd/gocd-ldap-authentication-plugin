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

import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.*;
import cd.go.framework.ldap.Ldap;
import cd.go.framework.ldap.LdapFactory;
import cd.go.framework.ldap.mapper.AbstractMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LdapAuthenticatorTest {

    private AuthConfig authConfig;
    private LdapFactory ldapFactory;
    private LdapConfiguration ldapConfiguration;
    private Ldap ldap;
    private Credentials credentials;
    private LdapAuthenticator ldapAuthenticator;

    @BeforeEach
    void setUp() {
        authConfig = mock(AuthConfig.class);
        ldapFactory = mock(LdapFactory.class);
        ldapConfiguration = mock(LdapConfiguration.class);
        ldap = mock(Ldap.class);

        credentials = new Credentials("username", "password");
        ldapAuthenticator = new LdapAuthenticator(ldapFactory);

        when(authConfig.getId()).thenReturn("id");
        when(authConfig.getConfiguration()).thenReturn(ldapConfiguration);
        when(ldapFactory.ldapForConfiguration(ldapConfiguration)).thenReturn(ldap);
    }


    @Test
    void authenticate_shouldAuthenticateUserWithLdap() throws Exception {
        ldapAuthenticator.authenticate(credentials, Collections.singletonList(authConfig));

        verify(ldap).authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(AbstractMapper.class));
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponseWithUserOnSuccessfulAuthentication() throws Exception {
        final UserMapper userMapper = mock(UserMapper.class);
        final User user = new User("jduke", "Java Duke", "jduke2example.com");
        Attributes attributes = new BasicAttributes();

        when(ldap.authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(AbstractMapper.class))).thenReturn(attributes);
        when(ldapConfiguration.getUserMapper(new UsernameResolver(credentials.getUsername()))).thenReturn(userMapper);
        when(userMapper.mapFromResult(attributes)).thenReturn(user);

        final AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(authenticationResponse.getUser()).isEqualTo(user);
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponseWithAuthConfigOnSuccessfulAuthentication() throws Exception {
        final UserMapper userMapper = mock(UserMapper.class);
        final AuthConfig validAuthConfig = mock(AuthConfig.class);
        final LdapConfiguration validLdapConfiguration = mock(LdapConfiguration.class);
        Attributes attributes = new BasicAttributes();

        when(validAuthConfig.getConfiguration()).thenReturn(validLdapConfiguration);
        when(ldapFactory.ldapForConfiguration(validAuthConfig.getConfiguration())).thenReturn(ldap);
        when(ldap.authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(AbstractMapper.class))).thenThrow(new RuntimeException()).thenReturn(attributes);
        when(validLdapConfiguration.getUserMapper(new UsernameResolver(credentials.getUsername()))).thenReturn(userMapper);
        when(userMapper.mapFromResult(attributes)).thenReturn(mock(User.class));

        final AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(credentials, Arrays.asList(this.authConfig, validAuthConfig));

        assertThat(authenticationResponse.getConfigUsedForAuthentication()).isEqualTo(validAuthConfig);
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponseWithAuthConfigUsedForAuthenticationInCaseOfMultipleAuthConfigs() throws Exception {
        final UserMapper userMapper = mock(UserMapper.class);
        Attributes attributes = new BasicAttributes();

        when(ldapConfiguration.getLdapUrlAsString()).thenReturn("some-url");
        when(ldap.authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(AbstractMapper.class))).thenReturn(attributes);
        when(ldapConfiguration.getUserMapper(new UsernameResolver(credentials.getUsername()))).thenReturn(userMapper);
        when(userMapper.mapFromResult(attributes)).thenReturn(mock(User.class));

        final AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(authenticationResponse.getConfigUsedForAuthentication()).isEqualTo(authConfig);
    }
}