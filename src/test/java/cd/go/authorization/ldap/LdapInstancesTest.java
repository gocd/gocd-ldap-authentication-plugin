/*
 * Copyright 2016 ThoughtWorks, Inc.
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

package cd.go.authorization.ldap;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class LdapInstancesTest extends BaseTest {

    @Test
    public void shouldAbleToGetLdapSearchObject() throws Exception {
        LdapInstances ldapInstances = new LdapInstances();
        assertThat(ldapInstances.getPluginProfile(), hasSize(0));

        ldapInstances.createLdapSearchInstances(Collections.singletonMap("ldap", pluginConfig));
        assertThat(ldapInstances.getPluginProfile(), hasSize(1));
    }
}