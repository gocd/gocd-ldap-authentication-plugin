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

package cd.go.framework.ldap;

import cd.go.authentication.ldap.model.LdapConfiguration;
import cd.go.framework.ldap.filter.EqualsFilter;
import cd.go.framework.ldap.filter.Filter;
import cd.go.framework.ldap.mapper.AbstractMapper;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Ldap {
    private LdapConfiguration ldapConfiguration;
    private final int MAX_AUTHENTICATION_RESULT = 1;

    public Ldap(LdapConfiguration ldapConfiguration) {
        this.ldapConfiguration = ldapConfiguration;
    }

    private DirContext getDirContext(LdapConfiguration ldapConfiguration, String username, String password) throws NamingException {
        Hashtable environments = new Environment(ldapConfiguration, true).getEnvironments();
        environments.put(Context.SECURITY_PRINCIPAL, username);
        environments.put(Context.SECURITY_CREDENTIALS, password);
        return new InitialDirContext(environments);
    }

    private void authenticate(LdapConfiguration ldapConfiguration, String username, String password) throws NamingException {
        getDirContext(ldapConfiguration, username, password).close();
    }

    public void validate() throws NamingException {
        authenticate(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());
    }

    private static SearchControls getSimpleSearchControls(int maxResult) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (maxResult != 0)
            searchControls.setCountLimit(maxResult);
        return searchControls;
    }

    public <T> T authenticate(String username, String password, AbstractMapper<T> mapper) throws NamingException {
        NamingEnumeration<SearchResult> results = search(ldapConfiguration.getUserLoginFilter(), new String[]{username}, MAX_AUTHENTICATION_RESULT);

        if (results == null || !results.hasMore())
            throw new RuntimeException("User " + username + " is not exist in " + ldapConfiguration.getLdapUrl());

        while (results.hasMore()) {
            SearchResult searchResult = results.next();
            Attributes attributes = searchResult.getAttributes();
            String userDn = searchResult.getNameInNamespace();
            attributes.put(new BasicAttribute("dn", userDn));
            authenticate(ldapConfiguration, userDn, password);
            return mapper.mapFromResult(attributes);
        }

        results.close();
        return null;
    }

    public <T> List<T> search(String filter, Object[] filterArgs, AbstractMapper<T> mapper, int maxResult) throws NamingException {
        List<T> results = new ArrayList<T>();
        NamingEnumeration<SearchResult> searchResults = search(filter, filterArgs, maxResult);
        if (searchResults == null)
            return results;

        while (searchResults.hasMore()) {
            results.add(mapper.mapFromResult(searchResults.next().getAttributes()));
        }
        searchResults.close();
        return results;
    }

    public <T> List<T> searchGroup(String searchBase, String filter, AbstractMapper<T> mapper) throws NamingException {
        List<T> results = new ArrayList<>();

        DirContext dirContext = getDirContext(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());
        NamingEnumeration<SearchResult> searchResult = dirContext.search(searchBase, filter, getSimpleSearchControls(0));

        while (searchResult.hasMore()) {
            results.add(mapper.mapFromResult(searchResult.next().getAttributes()));
        }
        return results;
    }

    private NamingEnumeration<SearchResult> search(String filter, Object[] filterArgs, int maxResult) throws NamingException {
        DirContext dirContext = getDirContext(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());
        for (String base : ldapConfiguration.getSearchBases()) {
            NamingEnumeration<SearchResult> results = dirContext.search(base, filter, filterArgs, getSimpleSearchControls(maxResult));
            if (results.hasMore())
                return results;
        }
        return null;
    }
}
