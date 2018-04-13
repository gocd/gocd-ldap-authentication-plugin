/*
 * Copyright 2018 ThoughtWorks, Inc.
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
import static java.text.MessageFormat.format;
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
            List<SearchResult> results = search(dirContext, ldapConfiguration.getUserLoginFilter(), new String[]{username}, MAX_AUTHENTICATION_RESULT, true);

            if (results.isEmpty()) {
                throw new RuntimeException(format("User {0} does not exist in {1}", username, ldapConfiguration.getLdapUrl()));
            }

            SearchResult searchResult = results.get(0);
            Attributes attributes = searchResult.getAttributes();
            String userDn = searchResult.getNameInNamespace();
            attributes.put(new BasicAttribute("dn", userDn));
            authenticate(ldapConfiguration, userDn, password);
            return mapper.mapFromResult(attributes);

        } catch (SearchResultLimitExceededException e) {
            throw new RuntimeException(foundMultipleUserForAuthenticationErrorMessage(username, e));
        } finally {
            closeContextSilently(dirContext);
        }
    }

    private String foundMultipleUserForAuthenticationErrorMessage(String username, SearchResultLimitExceededException e) {
        StringBuilder messageBuilder = new StringBuilder()
                .append(format("Found multiple users in search base `{0}` with username `{1}`. ", e.getSearchBase(), username));
        if (ldapConfiguration.getUserLoginFilter().contains("*")) {
            messageBuilder.append("It is not recommended to have wildcard(`*{0}*`, `{0}*` or `*{0}`) in `UserLoginFilter` field as it can match other users.");
        }
        return messageBuilder.toString();
    }

    public <T> List<T> search(String filter, Object[] filterArgs, AbstractMapper<T> mapper, int maxResult) throws NamingException {
        List<T> results = new ArrayList<>();
        DirContext dirContext = getDirContext(ldapConfiguration, ldapConfiguration.getManagerDn(), ldapConfiguration.getPassword());

        try {
            List<SearchResult> searchResults = search(dirContext, filter, filterArgs, maxResult, false);

            for (SearchResult result : searchResults) {
                results.add(mapper.mapFromResult(result.getAttributes()));
            }
        } finally {
            closeContextSilently(dirContext);
        }

        return results;
    }

    private final DirContext getDirContext(LdapConfiguration ldapConfiguration, String username, String password) throws NamingException {
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

    private List<SearchResult> search(DirContext context, String filter, Object[] filterArgs, int maxResult, boolean isHardLimitOnMaxResult) throws NamingException {
        final List<SearchResult> results = new ArrayList<>();

        if (maxResult == 0) {
            return results;
        }

        for (String base : ldapConfiguration.getSearchBases()) {
            final int remainingResultCount = maxResult - results.size();

            final List<SearchResult> searchResultsFromSearchBase = searchInBase(context, base, filter, filterArgs, remainingResultCount, isHardLimitOnMaxResult);
            results.addAll(searchResultsFromSearchBase);

            if (results.size() >= maxResult) {
                break;
            }
        }

        return results;
    }

    private List<SearchResult> searchInBase(DirContext context, String base, String filter, Object[] filterArgs, int maxResult, boolean isHardLimitOnMaxResult) throws NamingException {
        final List<SearchResult> results = new ArrayList<>();

        if (maxResult == 0) {
            return results;
        }

        NamingEnumeration<SearchResult> searchResults = null;
        try {
            LOG.debug(format("Searching user in search base {0} using search filter {1}.", base, filter));
            searchResults = context.search(base, filter, filterArgs, getSimpleSearchControls(maxResult));
            while (searchResults.hasMoreElements() && results.size() < maxResult) {
                results.add(searchResults.next());
            }

            if (isHardLimitOnMaxResult && searchResults.hasMoreElements()) {
                throw new SearchResultLimitExceededException(maxResult, base);
            }

            if (results.size() >= maxResult) {
                return results;
            }
        } finally {
            closeNamingEnumerationSilently(searchResults);
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
        searchControls.setTimeLimit(5000);
        if (maxResult != 0) {
            searchControls.setCountLimit(maxResult);
        }
        return searchControls;
    }

    void closeContextSilently(DirContext dirContext) {
        if (dirContext == null) {
            return;
        }

        try {
            dirContext.close();
        } catch (Exception e) {
            LOG.error("Error closing ldap connection", e);
        }
    }

    void closeNamingEnumerationSilently(NamingEnumeration namingEnumeration) {
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