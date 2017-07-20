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

package cd.go.framework.ldap;

import cd.go.authentication.ldap.BaseIntegrationTest;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.authentication.ldap.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.naming.AuthenticationException;
import javax.naming.directory.DirContext;
import java.util.List;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LdapIntegrationTest extends BaseIntegrationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldAuthenticateUser() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final User user = ldapSpy.authenticate("bford", "bob", ldapConfiguration.getUserMapper(new UsernameResolver()));

        assertNotNull(user);
        assertThat(user, is(new User("bford", "Bob Ford", "bford@example.com")));

        verify(ldapSpy, times(2)).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void shouldErrorOutIfFailToAuthenticateUser() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        thrown.expect(AuthenticationException.class);
        thrown.expectMessage("[LDAP: error code 49 - INVALID_CREDENTIALS: Bind failed: ERR_229 Cannot authenticate user uid=bob,ou=Employees,ou=Enterprise,ou=Principal,ou=system]");

        ldapSpy.authenticate("bford", "wrong-password", ldapConfiguration.getUserMapper(new UsernameResolver()));

        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void shouldErrorOutUserIsNotExistInLdap() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(format("User foo does not exist in {0}", ldapConfiguration.getLdapUrl()));

        ldapSpy.authenticate("foo", "bar", ldapConfiguration.getUserMapper(new UsernameResolver()));

        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void shouldSearchUser() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final List<User> users = ldapSpy.search("(uid=*{0}*)", new String[]{"pbanks"}, ldapConfiguration.getUserMapper(new UsernameResolver()), 1);

        assertThat(users, hasSize(1));
        assertThat(users.get(0), is(new User("pbanks", "P.Banks", "pbanks@example.com")));
        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }

    @Test
    public void shouldSearchUsersFromMultipleSearchBases() throws Exception {
        LdapConfiguration ldapConfiguration = ldapConfiguration(new String[]{"ou=Employees,ou=Enterprise,ou=Principal,ou=system", "ou=Clients,ou=Enterprise,ou=Principal,ou=system"});

        final Ldap ldapSpy = spy(new Ldap(ldapConfiguration));

        final List<User> users = ldapSpy.search("(uid=*{0}*)", new String[]{"banks"}, ldapConfiguration.getUserMapper(new UsernameResolver()), 2);

        assertThat(users, hasSize(2));
        assertThat(users, containsInAnyOrder(new User("pbanks", "P.Banks", "pbanks@example.com"), new User("sbanks", "S.Banks", "sbanks@example.com")));
        verify(ldapSpy).closeContextSilently(any(DirContext.class));
    }
}
