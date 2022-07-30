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

package cd.go.authentication.ldap;

import cd.go.authentication.ldap.mapper.*;
import cd.go.authentication.ldap.model.*;
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
    private LdapClient ldapClient;
    private Credentials credentials;
    private LdapAuthenticator ldapAuthenticator;
    private LdapMapperFactory ldapMapperFactory;

    @BeforeEach
    void setUp() {
        authConfig = mock(AuthConfig.class);
        ldapFactory = mock(LdapFactory.class);
        ldapMapperFactory = mock(LdapMapperFactory.class);
        ldapConfiguration = mock(LdapConfiguration.class);
        ldapClient = mock(LdapClient.class);

        credentials = new Credentials("username", "password");
        ldapAuthenticator = new LdapAuthenticator(ldapFactory, ldapMapperFactory);

        when(authConfig.getId()).thenReturn("id");
        when(authConfig.getConfiguration()).thenReturn(ldapConfiguration);
        when(ldapFactory.ldapForConfiguration(ldapConfiguration)).thenReturn(ldapClient);
        when(ldapMapperFactory.attributeOrEntryMapper()).thenReturn(mock(Mapper.class));
    }

    @Test
    void authenticate_shouldAuthenticateUserWithLdap() {
        ldapAuthenticator.authenticate(credentials, Collections.singletonList(authConfig));

        verify(ldapClient).authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(Mapper.class));
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponseWithUserOnSuccessfulAuthentication() {
        final UserMapper userMapper = mock(UserMapper.class);
        final User user = new User("jduke", "Java Duke", "jduke2example.com");
        Attributes attributes = new BasicAttributes();

        when(ldapClient.authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(Mapper.class))).thenReturn(attributes);
        when(ldapConfiguration.getUserMapper(new UsernameResolver(credentials.getUsername()))).thenReturn(userMapper);
        when(userMapper.mapObject(new ResultWrapper(attributes))).thenReturn(user);

        final AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(authenticationResponse.getUser()).isEqualTo(user);
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponseWithAuthConfigOnSuccessfulAuthentication() {
        final UserMapper userMapper = mock(UserMapper.class);
        final AuthConfig validAuthConfig = mock(AuthConfig.class);
        final LdapConfiguration validLdapConfiguration = mock(LdapConfiguration.class);
        Attributes attributes = new BasicAttributes();

        when(validAuthConfig.getConfiguration()).thenReturn(validLdapConfiguration);
        when(ldapFactory.ldapForConfiguration(validAuthConfig.getConfiguration())).thenReturn(ldapClient);
        when(ldapClient.authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(Mapper.class))).thenThrow(new RuntimeException()).thenReturn(attributes);
        when(validLdapConfiguration.getUserMapper(new UsernameResolver(credentials.getUsername()))).thenReturn(userMapper);
        when(userMapper.mapObject(new ResultWrapper(attributes))).thenReturn(mock(User.class));

        final AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(credentials, Arrays.asList(this.authConfig, validAuthConfig));

        assertThat(authenticationResponse.getConfigUsedForAuthentication()).isEqualTo(validAuthConfig);
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponseWithAuthConfigUsedForAuthenticationInCaseOfMultipleAuthConfigs() throws Exception {
        final UserMapper userMapper = mock(UserMapper.class);
        Attributes attributes = new BasicAttributes();

        when(ldapConfiguration.getLdapUrlAsString()).thenReturn("some-url");
        when(ldapClient.authenticate(eq(credentials.getUsername()), eq(credentials.getPassword()), any(Mapper.class))).thenReturn(attributes);
        when(ldapConfiguration.getUserMapper(new UsernameResolver(credentials.getUsername()))).thenReturn(userMapper);
        when(userMapper.mapObject(new ResultWrapper(attributes))).thenReturn(mock(User.class));

        final AuthenticationResponse authenticationResponse = ldapAuthenticator.authenticate(credentials, Collections.singletonList(authConfig));

        assertThat(authenticationResponse.getConfigUsedForAuthentication()).isEqualTo(authConfig);
    }
}