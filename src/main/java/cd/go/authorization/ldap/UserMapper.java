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

import cd.go.authorization.ldap.models.User;
import cd.go.framework.ldap.mapper.AbstractMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import static cd.go.authorization.ldap.LdapPlugin.LOG;

public class UserMapper extends AbstractMapper<User> {

    private final String usernameAttribute;
    private final String displayNameAttribute;
    private final String emailAttribute;

    public UserMapper(String usernameAttribute, String displayNameAttribute, String emailAttribute) {
        this.usernameAttribute = usernameAttribute;
        this.displayNameAttribute = displayNameAttribute;
        this.emailAttribute = emailAttribute;
    }

    @Override
    public User mapFromResult(Attributes attributes) throws NamingException {
        return new User(resolveAttribute(usernameAttribute, attributes),
                resolveAttribute(displayNameAttribute, attributes),
                resolveAttribute(emailAttribute, attributes));
    }

    private String resolveAttribute(String attributeName, Attributes attributes) throws NamingException {
        try {
            Attribute attribute = attributes.get(attributeName);
            return attribute.get().toString();
        } catch (NullPointerException e) {
            LOG.error("Failed to get attribute `" + attributeName + "` value.");
            throw new NamingException("Failed to get attribute `" + attributeName + "` value.");
        }
    }



}
