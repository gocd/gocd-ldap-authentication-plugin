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

package cd.go.apacheds;

import cd.go.authentication.ldap.model.LdapConfiguration;
import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapApiServiceFactory;
import org.apache.directory.api.ldap.extras.controls.ppolicy_impl.PasswordPolicyDecorator;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.template.AbstractPasswordPolicyResponder;
import org.apache.directory.ldap.client.template.EntryMapper;
import org.apache.directory.ldap.client.template.LdapConnectionTemplate;
import org.apache.directory.ldap.client.template.PasswordWarning;
import org.apache.directory.ldap.client.template.exception.PasswordException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cd.go.apacheds.pool.ConnectionPoolFactory.getLdapConnectionPool;
import static cd.go.authentication.ldap.LdapPlugin.LOG;
import static java.text.MessageFormat.format;

public class Ldap {
    private final LdapConnectionTemplate ldapConnectionTemplate;
    private final LdapConfiguration ldapConfiguration;
    private ConnectionConfiguration connectionConfiguration;

    public Ldap(LdapConfiguration ldapConfiguration) {
        this.ldapConfiguration = ldapConfiguration;
        this.connectionConfiguration = new ConnectionConfiguration(ldapConfiguration);
        this.ldapConnectionTemplate = new LdapConnectionTemplate(getLdapConnectionPool(connectionConfiguration));
    }

    protected Ldap(LdapConfiguration ldapConfiguration, LdapConnectionTemplate ldapConnectionTemplate) {
        this.ldapConfiguration = ldapConfiguration;
        this.ldapConnectionTemplate = ldapConnectionTemplate;
    }

    public <T> T authenticate(String username, String password, EntryMapper<T> mapper) throws PasswordException {
        Entry entry = getLdapEntryFor(username);

        try {
            final PasswordWarning warning = preformBind(entry.getDn(), password);

            if (warning != null) {
                LOG.warn(format("Your password will expire in {0} seconds", warning.getTimeBeforeExpiration()));
                LOG.warn(format("Remaining authentications before the account will be locked - {0}", warning.getGraceAuthNsRemaining()));
                LOG.warn(format("Password reset is required - {0}", warning.isChangeAfterReset()));
            }

            return mapper.map(entry);
        } catch (LdapException e) {
            throw new cd.go.authentication.ldap.exception.LdapException(format("Failed to authenticate user `{0}` with ldap server {1}", username, ldapConfiguration.getLdapUrlAsString()));
        }
    }

    public <T> T searchUser(String username, EntryMapper<T> mapper) {
        Entry entry = getLdapEntryFor(username);

        try {
            return mapper.map(entry);
        } catch (LdapException e) {
            throw new cd.go.authentication.ldap.exception.LdapException(format("Failed to search user `{0}` with ldap server {1}", username, ldapConfiguration.getLdapUrlAsString()));
        }
    }


    private PasswordWarning preformBind(Dn userDn, String password) throws PasswordException {
        final LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        final LdapConnectionConfig connectionConfig = connectionConfiguration.toLdapConnectionConfig(userDn.getName(), password);
        final BindRequest bindRequest = new BindRequestImpl()
                .setName(userDn.getName())
                .setCredentials(password)
                .addControl(new PasswordPolicyDecorator(ldapApiService));

        LOG.debug("Performing bind using userDn `{0}`.");
        return new AbstractPasswordPolicyResponder(ldapApiService) {
        }.process(() -> {
            try (LdapNetworkConnection ldapNetworkConnection = new LdapNetworkConnection(connectionConfig)) {
                return ldapNetworkConnection.bind(bindRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> List<T> search(final String filter, final Object[] filterArgs, final EntryMapper<T> mapper, final int maxResultCount) {
        final List<T> searchResults = new ArrayList<>();
        for (String searchBase : ldapConfiguration.getSearchBases()) {
            int resultsToFetch = resultsToFetch(maxResultCount, searchResults.size());

            if (resultsToFetch == -1) {
                break;
            }

            try {
                final SearchRequest searchRequest = new SearchRequestImpl()
                        .setScope(SearchScope.SUBTREE)
                        .addAttributes("*")
                        .setSizeLimit(resultsToFetch)
                        .setFilter(format(filter, filterArgs))
                        .setTimeLimit(ldapConfiguration.getSearchTimeout())
                        .setBase(new Dn(searchBase));

                searchResults.addAll(ldapConnectionTemplate.search(searchRequest, mapper));
            } catch (LdapException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return searchResults;
    }

    public List<Entry> search(final String filter, final Object[] filterArgs, final int maxResultCount) {
        return search(filter, filterArgs, entry -> entry, maxResultCount);
    }

    public <T> List<T> searchGroup(List<String> searchBases, String filter, EntryMapper<T> mapper) {
        final List<T> searchResults = new ArrayList<>();

        for (String searchBase : searchBases) {
            try {

                final SearchRequest searchRequest = new SearchRequestImpl()
                        .setScope(SearchScope.SUBTREE)
                        .addAttributes("dn")
                        .setSizeLimit(0)
                        .setFilter(filter)
                        .setTimeLimit(ldapConfiguration.getSearchTimeout())
                        .setBase(new Dn(searchBase));

                searchResults.addAll(ldapConnectionTemplate.search(searchRequest, mapper));
            } catch (LdapException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return searchResults;
    }

    public void verifyConnection() {
        final String filter = format(ldapConfiguration.getUserSearchFilter(), "test");
        ldapConnectionTemplate.searchFirst(ldapConfiguration.getSearchBases().get(0), filter, SearchScope.SUBTREE, entry -> entry);
    }

    private int resultsToFetch(final int maxResultCount, final int resultCount) {
        return maxResultCount == 0 ? 0 : maxResultCount > resultCount ? maxResultCount - resultCount : -1;
    }

    private Entry getLdapEntryFor(String username) {
        final List<Entry> results = search(ldapConfiguration.getUserLoginFilter(), new String[]{username}, entry -> entry, 1);

        if (results.isEmpty()) {
            throw new RuntimeException(format("User {0} does not exist in {1}", username, ldapConfiguration.getLdapUrlAsString()));
        }

        return results.get(0);
    }
}
