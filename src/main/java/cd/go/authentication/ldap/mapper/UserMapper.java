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

package cd.go.authentication.ldap.mapper;

import cd.go.authentication.ldap.LdapPlugin;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.mapper.AbstractMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

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
        User user = new User(resolveAttribute(usernameAttribute, attributes),
                resolveAttribute(displayNameAttribute, attributes),
                resolveAttribute(emailAttribute, attributes), attributes);

        return user;
    }

    private String resolveAttribute(String attributeName, Attributes attributes) {
        try {
            Attribute attribute = attributes.get(attributeName);
            return attribute.get().toString();
        } catch (NullPointerException | NamingException e) {
            LdapPlugin.LOG.error("Failed to get attribute `" + attributeName + "` value.");
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserMapper that = (UserMapper) o;

        if (usernameAttribute != null ? !usernameAttribute.equals(that.usernameAttribute) : that.usernameAttribute != null)
            return false;
        if (displayNameAttribute != null ? !displayNameAttribute.equals(that.displayNameAttribute) : that.displayNameAttribute != null)
            return false;
        return emailAttribute != null ? emailAttribute.equals(that.emailAttribute) : that.emailAttribute == null;
    }

    @Override
    public int hashCode() {
        int result = usernameAttribute != null ? usernameAttribute.hashCode() : 0;
        result = 31 * result + (displayNameAttribute != null ? displayNameAttribute.hashCode() : 0);
        result = 31 * result + (emailAttribute != null ? emailAttribute.hashCode() : 0);
        return result;
    }
}
