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

package cd.go.framework.ldap;

import cd.go.authentication.ldap.model.LdapConfiguration;

import javax.naming.Context;
import java.util.Hashtable;

public class Environment {

    private static final String AUTHENTICATION_TYPE = "simple";
    private static final String LDAP_LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private LdapConfiguration ldapConfiguration;

    public Environment(LdapConfiguration ldapConfiguration) {
        this.ldapConfiguration = ldapConfiguration;
    }

    public Hashtable getEnvironments() {
        Hashtable<String, Object> environments = new Hashtable<>(10);
        environments.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_LDAP_CTX_FACTORY);
        environments.put(Context.PROVIDER_URL, ldapConfiguration.getLdapUrlAsString());
        environments.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION_TYPE);
        return environments;
    }
}
