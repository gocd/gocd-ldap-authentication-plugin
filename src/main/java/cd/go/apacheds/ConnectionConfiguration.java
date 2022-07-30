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

package cd.go.apacheds;

import cd.go.authentication.ldap.model.LdapConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;

import java.util.Objects;

public class ConnectionConfiguration {
    private boolean useSsl;
    private int ldapPort;
    private String ldapHost;
    private String managerDn;
    private String password;


    public ConnectionConfiguration(LdapConfiguration ldapConfiguration) {
        this.ldapHost = ldapConfiguration.getLdapUrl().getHost();
        this.ldapPort = getPort(ldapConfiguration);
        this.useSsl = ldapConfiguration.useSSL();
        this.managerDn = ldapConfiguration.getManagerDn();
        this.password = ldapConfiguration.getPassword();
    }

    private int getPort(LdapConfiguration ldapConfiguration) {
        final int port = ldapConfiguration.getLdapUrl().getPort();

        if (port != -1) {
            return port;
        }

        return ldapConfiguration.useSSL() ? 636 : 389;
    }

    public LdapConnectionConfig toLdapConnectionConfig() {
        return toLdapConnectionConfig(this.managerDn, this.password);
    }

    public LdapConnectionConfig toLdapConnectionConfig(String dn, String password) {
        final LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost(this.ldapHost);
        config.setLdapPort(this.ldapPort);
        config.setUseSsl(this.useSsl);

        if (StringUtils.isNoneBlank(dn, password)) {
            config.setName(dn);
            config.setCredentials(password);
        }

        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionConfiguration that = (ConnectionConfiguration) o;
        return useSsl == that.useSsl &&
                ldapPort == that.ldapPort &&
                Objects.equals(ldapHost, that.ldapHost) &&
                Objects.equals(managerDn, that.managerDn) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(useSsl, ldapPort, ldapHost, managerDn, password);
    }

    @Override
    public String toString() {
        return "ConnectionConfiguration{" +
                "useSsl=" + useSsl +
                ", ldapPort=" + ldapPort +
                ", ldapHost='" + ldapHost + '\'' +
                ", managerDn='" + managerDn + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
