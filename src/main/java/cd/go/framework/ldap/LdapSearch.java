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

package cd.go.framework.ldap;

import cd.go.authorization.ldap.PluginConfiguration;
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

public class LdapSearch {

    private static final String COM_SUN_JNDI_LDAP_LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String AUTHENTICATION_TYPE = "simple";

    private DirContext dirContext;
    private PluginConfiguration pluginConfiguration;


    private LdapSearch(PluginConfiguration pluginConfiguration, DirContext dirContext) {
        this.pluginConfiguration = pluginConfiguration;
        this.dirContext = dirContext;
    }

    public static LdapSearch getInstance(PluginConfiguration pluginConfiguration) {
        try {
            DirContext dirContext = LdapSearch.authenticate(pluginConfiguration, pluginConfiguration.getManagerDn(), pluginConfiguration.getPassword());
            LdapSearch ldapSearch = new LdapSearch(pluginConfiguration, dirContext);
            return ldapSearch;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private static DirContext authenticate(PluginConfiguration pluginConfiguration, String username, String password) throws NamingException {
        Hashtable env = getEnv(pluginConfiguration);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        return new InitialDirContext(env);
    }

    public <T> T authenticateUser(String username, String password, AbstractMapper<T> mapper) throws NamingException {
        Filter filter = new EqualsFilter(pluginConfiguration.getSearchFilter(), username);
        NamingEnumeration<SearchResult> results = search(filter, 1);

        if (results == null || !results.hasMore())
            throw new RuntimeException("User " + username + " is not exist in " + pluginConfiguration.getLdapUrl());

        while (results.hasMore()) {
            SearchResult searchResult = results.next();
            Attributes attributes = searchResult.getAttributes();
            String userDn = searchResult.getNameInNamespace();
            authenticate(this.pluginConfiguration, userDn, password);
            return mapper.mapFromResult(attributes);
        }
        return null;
    }

    public <T> List<T> search(Filter filter, AbstractMapper<T> mapper, int maxResult) throws NamingException {
        List<T> results = new ArrayList<T>();
        NamingEnumeration<SearchResult> searchResults = search(filter, maxResult);
        if (searchResults == null)
            return results;

        while (searchResults.hasMore()) {
            results.add(mapper.mapFromResult(searchResults.next().getAttributes()));
        }
        return results;
    }

    public <T> List<T> search(Filter filter, AbstractMapper<T> mapper) throws NamingException {
        return search(filter, mapper, 0);
    }


    private NamingEnumeration<SearchResult> search(Filter filter, int maxResult) throws NamingException {
        for (String base : pluginConfiguration.getSearchBase()) {
            NamingEnumeration<SearchResult> results = dirContext.search(base, filter.prepare(), getSimpleSearchControls(maxResult));
            if (results.hasMore())
                return results;
        }
        return null;
    }

    private SearchControls getSimpleSearchControls(int maxResult) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (maxResult != 0)
            searchControls.setCountLimit(maxResult);
        return searchControls;
    }

    private static Hashtable getEnv(PluginConfiguration pluginConfiguration) {
        Hashtable env = new Hashtable(6);
        env.put(Context.INITIAL_CONTEXT_FACTORY, COM_SUN_JNDI_LDAP_LDAP_CTX_FACTORY);
        env.put(Context.PROVIDER_URL, pluginConfiguration.getLdapUrl());
        env.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION_TYPE);
        return env;
    }

}
