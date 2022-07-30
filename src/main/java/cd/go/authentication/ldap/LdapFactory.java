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

import cd.go.apacheds.ApacheDsLdapClient;
import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.JNDILdapClient;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.PluginSystemProperty.useJNDIClient;

public class LdapFactory {

    public LdapClient ldapForConfiguration(LdapConfiguration configuration) {
        boolean useJndiClient = useJNDIClient();

        if (useJndiClient) {
            LOG.debug("Using JDNI based ldap client as user has specified system property 'use.jndi.ldap.client=true'");
            return new JNDILdapClient(configuration);
        }

        LOG.debug("Using apache ds ldap client.");
        return new ApacheDsLdapClient(configuration);
    }
}
