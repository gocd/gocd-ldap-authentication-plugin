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
import cd.go.framework.ldap.mapper.AbstractMapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static cd.go.authentication.ldap.utils.Util.isNotBlank;
import static javax.naming.Context.SECURITY_CREDENTIALS;
import static javax.naming.Context.SECURITY_PRINCIPAL;

public class Ldap {
    private LdapConfiguration ldapConfiguration;
    private final int MAX_AUTHENTICATION_RESULT = 1;

    public Ldap(LdapConfiguration ldapConfiguration) {
        this.ldapConfiguration = ldapConfiguration;
    }

    public <T> T authenticate(String username, String password, AbstractMapper<T> mapper) throws NamingException {
        DirContext dirContext = getDirContext(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());

        try {
            List<SearchResult> results = search(dirContext, ldapConfiguration.getUserLoginFilter(), new String[]{username}, MAX_AUTHENTICATION_RESULT);

            if (results.isEmpty())
                throw new RuntimeException("User " + username + " does not exist in " + ldapConfiguration.getLdapUrl());

            SearchResult searchResult = results.get(0);
            Attributes attributes = searchResult.getAttributes();
            String userDn = searchResult.getNameInNamespace();
            attributes.put(new BasicAttribute("dn", userDn));
            authenticate(ldapConfiguration, userDn, password);
            return mapper.mapFromResult(attributes);

        } finally {
            closeContextSilently(dirContext);
        }
    }

    public <T> List<T> search(String filter, Object[] filterArgs, AbstractMapper<T> mapper, int maxResult) throws NamingException {
        List<T> results = new ArrayList<>();
        DirContext dirContext = getDirContext(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());

        try {
            List<SearchResult> searchResults = search(dirContext, filter, filterArgs, maxResult);

            for (SearchResult result : searchResults) {
                results.add(mapper.mapFromResult(result.getAttributes()));
            }
        } finally {
            closeContextSilently(dirContext);
        }

        return results;
    }

    private DirContext getDirContext(LdapConfiguration ldapConfiguration, String username, String password) throws NamingException {
        Hashtable<String, Object> environments = new Environment(ldapConfiguration).getEnvironments();
        if (isNotBlank(username)) {
            environments.put(SECURITY_PRINCIPAL, username);
            environments.put(SECURITY_CREDENTIALS, password);
        }

        InitialDirContext context = null;

        try {
            context = new InitialDirContext(environments);
        } catch (NamingException e) {
            closeContextSilently(context);
            throw e;
        }

        return context;
    }

    private List<SearchResult> search(DirContext context, String filter, Object[] filterArgs, int maxResult) throws NamingException {
        List<SearchResult> results = new ArrayList<>();
        for (String base : ldapConfiguration.getSearchBases()) {
            NamingEnumeration<SearchResult> searchResults = null;
            try {
                searchResults = context.search(base, filter, filterArgs, getSimpleSearchControls(maxResult));
                while (searchResults.hasMore() && results.size() < maxResult) {
                    results.add(searchResults.next());
                }
                if (results.size() >= maxResult) {
                    break;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            } finally {
                closeNamingEnumerationSilently(searchResults);
            }
        }

        return results;
    }

    private void authenticate(LdapConfiguration ldapConfiguration, String username, String password) throws NamingException {
        closeContextSilently(getDirContext(ldapConfiguration, username, password));
    }

    public void validate() throws NamingException {
        authenticate(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());
    }

    private static SearchControls getSimpleSearchControls(int maxResult) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(5000); // timeout after five seconds
        if (maxResult != 0)
            searchControls.setCountLimit(maxResult);
        return searchControls;
    }

    private void closeContextSilently(DirContext dirContext) {
        if (dirContext == null) {
            return;
        }

        try {
            dirContext.close();
        } catch (Exception e) {
            LOG.error("Error closing ldap connection", e);
        }
    }

    private void closeNamingEnumerationSilently(NamingEnumeration namingEnumeration) {
        if (namingEnumeration == null) {
            return;
        }

        try {
            namingEnumeration.close();
        } catch (Exception e) {
            LOG.error("Error closing naming enumeration", e);
        }
    }
}