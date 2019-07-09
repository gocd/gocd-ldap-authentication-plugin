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

import cd.go.authentication.ldap.exception.LdapException;
import cd.go.authentication.ldap.model.User;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static java.lang.String.format;

public class UserMapper implements Mapper<User> {
    private final String displayNameAttribute;
    private final String emailAttribute;
    private UsernameResolver usernameResolver;

    public UserMapper(UsernameResolver usernameResolver, String displayNameAttribute, String emailAttribute) {
        this.usernameResolver = usernameResolver;
        this.displayNameAttribute = displayNameAttribute;
        this.emailAttribute = emailAttribute;
    }

    @Override
    public User mapObject(ResultWrapper resultWrapper) {
        LOG.debug("Given object is type of {}", resultWrapper.getResult().getClass().getName());
        if (resultWrapper.getResult() instanceof Attributes) {
            return mapAttributes(resultWrapper);
        }

        if (resultWrapper.getResult() instanceof Entry) {
            return mapEntryToUser(resultWrapper);
        }

        throw new LdapException(format("Failed to map '%s' to %s", resultWrapper.getResult().getClass().getName(), User.class.getName()));
    }

    private User mapEntryToUser(ResultWrapper resultWrapper) {
        Entry entry = (Entry) resultWrapper.getResult();
        return new User(usernameResolver.getUsername(entry),
                resolveAttribute(displayNameAttribute, entry),
                resolveAttribute(emailAttribute, entry));
    }

    private User mapAttributes(ResultWrapper resultWrapper) {
        try {
            Attributes attributes = (Attributes) resultWrapper.getResult();
            return new User(usernameResolver.getUsername(attributes),
                    resolveAttribute(displayNameAttribute, attributes),
                    resolveAttribute(emailAttribute, attributes));
        } catch (NamingException e) {
            throw new LdapException(e);
        }
    }

    private String resolveAttribute(String attributeName, Attributes attributes) {
        try {
            Attribute attribute = attributes.get(attributeName);
            return attribute.get().toString();
        } catch (NullPointerException | NamingException e) {
            LOG.error("Failed to get attribute `" + attributeName + "` value.");
        }
        return null;
    }

    private String resolveAttribute(String attributeName, Entry entry) {
        try {
            return entry.containsAttribute(attributeName) ? entry.get(attributeName).getString() : null;
        } catch (LdapInvalidAttributeValueException e) {
            LOG.error("Failed to get attribute `" + attributeName + "` value.");
        }
        return null;
    }
}
