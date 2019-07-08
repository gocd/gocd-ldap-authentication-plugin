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

package cd.go.authentication.ldap.mapper;

import org.apache.directory.api.ldap.model.entry.Entry;

public class LdapMapperFactory {
    public static final String LDAP_USE_JNDI_CLIENT = "ldap.use.jndi.client";

    public Mapper attributeOrEntryMapper() {
        if (useJndiClient()) {
            return new AttributesMapper();
        }
        return (Mapper<Entry>) resultWrapper -> (Entry) resultWrapper.getResult();
    }

    private boolean useJndiClient() {
        return Boolean.parseBoolean(System.getProperty(LDAP_USE_JNDI_CLIENT));
    }
}
