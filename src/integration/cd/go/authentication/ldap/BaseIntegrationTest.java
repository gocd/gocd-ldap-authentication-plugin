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
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(FrameworkRunner.class)
@ApplyLdifFiles(value = "users.ldif", clazz = BaseIntegrationTest.class)
@CreateLdapServer(transports = {
        @CreateTransport(protocol = "LDAP")
})
public abstract class BaseIntegrationTest extends AbstractLdapTestUnit {

    protected LdapConfiguration ldapConfiguration(String[] searchBases) {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("Url", String.format("ldap://localhost:%s", ldapServer.getPort()));
        configuration.put("SearchBases", StringUtils.join(searchBases, "\n"));
        configuration.put("ManagerDN", "uid=admin,ou=system");
        configuration.put("Password", "secret");
        configuration.put("UserLoginFilter", "(uid={0})");
        configuration.put("UserSearchFilter", "(cn={0})");
        configuration.put("UserNameAttribute", "uid");
        configuration.put("DisplayNameAttribute", "displayName");

        return LdapConfiguration.fromJSON(new Gson().toJson(configuration));
    }

}
