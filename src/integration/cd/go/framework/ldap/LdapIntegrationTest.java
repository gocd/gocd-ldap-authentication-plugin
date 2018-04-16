/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package cd.go.framework.ldap;

import cd.go.authentication.ldap.BaseIntegrationTest;
import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.util.List;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LdapIntegrationTest extends BaseIntegrationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void authenticate_shouldAuthenticateUser() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final User user = ldapSpy.authenticate("bford", "bob", ldapConfiguration.getUserMapper(new UsernameResolver()));

        assertNotNull(user);
        assertThat(user, is(new User("bford", "Bob Ford", "bford@example.com")));

        verify(ldapSpy, times(2)).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void authenticate_shouldErrorOutIfFailToAuthenticateUser() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        thrown.expect(AuthenticationException.class);
        thrown.expectMessage("[LDAP: error code 49 - INVALID_CREDENTIALS: Bind failed: ERR_229 Cannot authenticate user uid=bob,ou=Employees,ou=Enterprise,ou=Principal,ou=system]");

        ldapSpy.authenticate("bford", "wrong-password", ldapConfiguration.getUserMapper(new UsernameResolver()));

        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void authenticate_shouldErrorOutUserIsNotExistInLdap() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(format("User foo does not exist in {0}", ldapConfiguration.getLdapUrl()));

        ldapSpy.authenticate("foo", "bar", ldapConfiguration.getUserMapper(new UsernameResolver()));

        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void authenticate_shouldErrorOutIfMultipleUserDetectedInSearchBaseWhenUserLoginFilterHasWildCard() throws NamingException {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"}, "(uid=*{0}*)");
        final Ldap ldap = new Ldap(ldapConfiguration);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Found multiple users in search base `ou=system` with username `neil`. It is not recommended to have wildcard(`*{0}*`, `{0}*` or `*{0}`) in `UserLoginFilter` field as it can match other users.");

        ldap.authenticate("neil", "neil", ldapConfiguration.getUserMapper(new UsernameResolver()));
    }

    @Test
    public void search_shouldSearchUser() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final List<User> users = ldapSpy.search("(uid=*{0}*)", new String[]{"pbanks"}, ldapConfiguration.getUserMapper(new UsernameResolver()), 1);

        assertThat(users, hasSize(1));
        assertThat(users.get(0), is(new User("pbanks", "P.Banks", "pbanks@example.com")));
        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void search_shouldSearchUsersFromMultipleSearchBases() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final List<User> users = ldapSpy.search("(uid=*{0}*)", new String[]{"banks"}, ldapConfiguration.getUserMapper(new UsernameResolver()), 2);

        assertThat(users, hasSize(2));
        assertThat(users, containsInAnyOrder(new User("pbanks", "P.Banks", "pbanks@example.com"), new User("sbanks", "S.Banks", "sbanks@example.com")));
        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }


    @Test
    public void search_shouldStopSearchingWhenSpecifiedNumberOfUsersFoundInFirstSearchBase() throws Exception {
        final LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"});
        final UserMapper userMapper = ldapConfiguration.getUserMapper(new UsernameResolver());

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final List<User> allUsers = ldapSpy.search("(uid=*{0}*)", new String[]{"a"}, userMapper, Integer.MAX_VALUE);

        assertThat(allUsers, hasSize(5));

        final List<User> userFoundFromFirstSearchBase = ldapSpy.search("(uid=*{0}*)", new String[]{"a"}, userMapper, 3);

        assertThat(userFoundFromFirstSearchBase, hasSize(3));
        verify(ldapSpy, times(2)).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void search_shouldSearchAcrossMultipleSearchBasesAndLimitTheSearchResult() throws Exception {
        final LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"});
        final UserMapper userMapper = ldapConfiguration.getUserMapper(new UsernameResolver());

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final List<User> allUsers = ldapSpy.search("(uid=*{0}*)", new String[]{"a"}, userMapper, Integer.MAX_VALUE);

        assertThat(allUsers, hasSize(5));

        final List<User> users = ldapSpy.search("(uid=*{0}*)", new String[]{"a"}, userMapper, 4);

        assertThat(users, hasSize(4));
        verify(ldapSpy, times(2)).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void validate_shouldValidateManagerDnAndPassword() {
        LdapConfiguration ldapConfiguration = ldapConfiguration("uid=admin,ou=system", "secret", "ou=system");

        try {
            new Ldap(ldapConfiguration).validate();
        } catch (Exception e) {
            fail("Should not error out when valid credentials provided");
        }
    }

    @Test
    public void validate_shouldErrorOutWhenInvalidManagerDnAndPasswordProvided() throws NamingException {
        LdapConfiguration ldapConfiguration = ldapConfiguration("uid=admin,ou=system", "invalid-password", "ou=system");

        thrown.expect(AuthenticationException.class);
        thrown.expectMessage("LDAP: error code 49 - INVALID_CREDENTIALS: Bind failed: ERR_229 Cannot authenticate user uid=admin,ou=system");

        new Ldap(ldapConfiguration).validate();
    }
}
