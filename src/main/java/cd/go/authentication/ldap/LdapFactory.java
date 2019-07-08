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

import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.JNDILdapClient;

public class LdapFactory {
    public static final String USE_JNDI_LDAP_CLIENT = "use.jndi.ldap.client";

    public LdapClient ldapForConfiguration(LdapConfiguration configuration) {
        boolean useJndiClient = Boolean.parseBoolean(System.getProperty(USE_JNDI_LDAP_CLIENT));

        if (useJndiClient) {
            return new JNDILdapClient(configuration);
        }

        return new cd.go.apacheds.Ldap(configuration);
    }
}
