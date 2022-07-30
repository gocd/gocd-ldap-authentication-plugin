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

package cd.go.authentication.ldap.mapper;

import cd.go.authentication.ldap.exception.InvalidUsernameException;
import org.apache.directory.api.ldap.model.entry.Entry;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.isNotBlank;


public class UsernameResolver {
    private String username;

    public UsernameResolver() {
    }

    public UsernameResolver(String username) {
        this.username = username;
    }

    public String getUsername(Attributes attributes) throws NamingException {
        if (isNotBlank(this.username)) {
            return this.username;
        }

        Attribute sAMAccountName = attributes.get("sAMAccountName");
        Attribute uid = attributes.get("uid");
        if (sAMAccountName == null && uid == null) {
            LOG.error("[User Search] Failed to resolve username using the attributes `sAMAccountName` and `uid`. ");
            throw new InvalidUsernameException("Username can not be blank. Failed to resolve username using the attributes `sAMAccountName` and `uid`.");
        }
        return sAMAccountName != null ? sAMAccountName.get().toString() : uid.get().toString();
    }

    public String getUsername(Entry entry) {
        if (isNotBlank(this.username)) {
            return this.username;
        }

        org.apache.directory.api.ldap.model.entry.Attribute sAMAccountName = entry.get("sAMAccountName");
        if (sAMAccountName != null) {
            return sAMAccountName.get().getString();
        }

        org.apache.directory.api.ldap.model.entry.Attribute uid = entry.get("uid");
        if (uid != null) {
            return uid.get().getString();
        }

        LOG.error("[User Search] Failed to resolve username using the attributes `sAMAccountName` and `uid`. ");
        throw new InvalidUsernameException("Username can not be blank. Failed to resolve username using the attributes `sAMAccountName` and `uid`.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsernameResolver that = (UsernameResolver) o;

        return username != null ? username.equals(that.username) : that.username == null;

    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
