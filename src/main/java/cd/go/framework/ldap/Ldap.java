package cd.go.framework.ldap;

import cd.go.authentication.ldap.model.LdapProfile;
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
    private LdapProfile ldapProfile;

    public Ldap(LdapProfile ldapProfile) {
        this.ldapProfile = ldapProfile;
    }

    private static DirContext getDirContext(LdapProfile ldapProfile, String username, String password) throws NamingException {
        Hashtable environments = new Environment(ldapProfile, true).getEnvironments();
        environments.put(Context.SECURITY_PRINCIPAL, username);
        environments.put(Context.SECURITY_CREDENTIALS, password);
        return new InitialDirContext(environments);
    }

    private static void authenticate(LdapProfile ldapProfile, String username, String password) throws NamingException {
        getDirContext(ldapProfile, username, password).close();
    }

    public static void validate(LdapProfile ldapProfile) throws NamingException {
        authenticate(ldapProfile, ldapProfile.getManagerDn(), ldapProfile.getPassword());
    }

    private static SearchControls getSimpleSearchControls(int maxResult) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (maxResult != 0)
            searchControls.setCountLimit(maxResult);
        return searchControls;
    }

    public <T> T authenticate(String username, String password, AbstractMapper<T> mapper) throws NamingException {
        Filter filter = new EqualsFilter(ldapProfile.getSearchFilter(), username);

        NamingEnumeration<SearchResult> results = search(filter, 1);
        if (results == null || !results.hasMore())
            throw new RuntimeException("User " + username + " is not exist in " + ldapProfile.getLdapUrl());

        while (results.hasMore()) {
            SearchResult searchResult = results.next();
            Attributes attributes = searchResult.getAttributes();
            String userDn = searchResult.getNameInNamespace();
            authenticate(ldapProfile, userDn, password);
            return mapper.mapFromResult(attributes);
        }

        results.close();
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
        searchResults.close();
        return results;
    }

    public <T> List<T> search(Filter filter, AbstractMapper<T> mapper) throws NamingException {
        return search(filter, mapper, 0);
    }

    private NamingEnumeration<SearchResult> search(Filter filter, int maxResult) throws NamingException {
        for (String base : ldapProfile.getSearchBases()) {
            DirContext dirContext = getDirContext(ldapProfile, ldapProfile.getManagerDn(), ldapProfile.getPassword());
            NamingEnumeration<SearchResult> results = dirContext.search(base, filter.prepare(), getSimpleSearchControls(maxResult));
            if (results.hasMore())
                return results;
        }
        return null;
    }
}
