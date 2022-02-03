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

import cd.go.apacheds.ApacheDsLdapClient;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.JNDILdapClient;
import cd.go.plugin.base.test_helper.system_extensions.annotations.SystemProperty;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static cd.go.authentication.ldap.PluginSystemProperty.USE_JNDI_LDAP_CLIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class LdapFactoryTest {
    private static final Class<ApacheDsLdapClient> APACHE_DS_CLIENT_CLASS = ApacheDsLdapClient.class;
    private static final Class<JNDILdapClient> JNDI_CLIENT_CLASS = JNDILdapClient.class;
    private LdapFactory ldapFactory;

    @Mock
    private LdapConfiguration ldapConfiguration;

    @BeforeEach
    void setUp() {
        openMocks(this);
        ldapFactory = new LdapFactory();

        when(ldapConfiguration.getLdapUrl()).thenReturn(new LdapUrl());
    }

    @Test
    @SystemProperty(key = USE_JNDI_LDAP_CLIENT, value = "true")
    void shouldReturnJndiClientWhenToggleIsOn() {
        assertThat(ldapFactory.ldapForConfiguration(null))
                .isInstanceOf(JNDI_CLIENT_CLASS);
    }

    @Test
    @SystemProperty(key = USE_JNDI_LDAP_CLIENT, value = "false")
    void shouldReturnApacheDsClientWhenToggleIsOff() {
        assertThat(ldapFactory.ldapForConfiguration(ldapConfiguration))
                .isInstanceOf(APACHE_DS_CLIENT_CLASS);
    }

    @Test
    @SystemProperty(key = USE_JNDI_LDAP_CLIENT, value = "daskdhaskjd")
    void shouldReturnApacheDsClientWhenToggleValueIsNotABoolean() {
        assertThat(ldapFactory.ldapForConfiguration(ldapConfiguration))
                .isInstanceOf(APACHE_DS_CLIENT_CLASS);
    }

    @Test
    void shouldReturnApacheDsClientWhenToggleValueIsNotGiven() {
        assertThat(ldapFactory.ldapForConfiguration(ldapConfiguration))
                .isInstanceOf(APACHE_DS_CLIENT_CLASS);
    }
}