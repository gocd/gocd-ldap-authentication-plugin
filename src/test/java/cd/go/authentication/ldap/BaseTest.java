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

import cd.go.authentication.ldap.model.LdapConfiguration;
import org.junit.Before;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {
    protected LdapConfiguration ldapConfiguration;

    @Before
    public void setup() {
        ldapConfiguration = mock(LdapConfiguration.class);

        when(ldapConfiguration.getLdapUrl()).thenReturn("ldap://localhost:10389");
        when(ldapConfiguration.getSearchBases()).thenReturn(Arrays.asList("ou=users,ou=system"));
        when(ldapConfiguration.getManagerDn()).thenReturn("uid=admin,ou=system");
        when(ldapConfiguration.getPassword()).thenReturn("secret");
        when(ldapConfiguration.getLoginAttribute()).thenReturn("uid");
        when(ldapConfiguration.getDisplayNameAttribute()).thenReturn("displayName");
        when(ldapConfiguration.getEmailAttribute()).thenReturn("mail");
        when(ldapConfiguration.getUserMapper()).thenCallRealMethod();
    }
}
