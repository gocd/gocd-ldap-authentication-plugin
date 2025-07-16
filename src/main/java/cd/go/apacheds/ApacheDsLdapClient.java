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

package cd.go.apacheds;

import cd.go.authentication.ldap.LdapClient;
import cd.go.authentication.ldap.exception.MultipleUserDetectedException;
import cd.go.authentication.ldap.mapper.Mapper;
import cd.go.authentication.ldap.model.LdapConfiguration;
import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapApiServiceFactory;
import org.apache.directory.api.ldap.extras.controls.ppolicy.PasswordPolicyRequestImpl;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.filter.FilterEncoder;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.template.AbstractPasswordPolicyResponder;
import org.apache.directory.ldap.client.template.LdapConnectionTemplate;
import org.apache.directory.ldap.client.template.PasswordWarning;
import org.apache.directory.ldap.client.template.exception.PasswordException;

import java.util.ArrayList;
import java.util.List;

import static cd.go.apacheds.pool.ConnectionPoolFactory.getLdapConnectionPool;
import static cd.go.authentication.ldap.LdapPlugin.LOG;

public class ApacheDsLdapClient implements LdapClient {
    private final LdapConnectionTemplate ldapConnectionTemplate;
    private final LdapConfiguration ldapConfiguration;
    private ConnectionConfiguration connectionConfiguration;

    public ApacheDsLdapClient(LdapConfiguration ldapConfiguration) {
        this.ldapConfiguration = ldapConfiguration;
        this.connectionConfiguration = new ConnectionConfiguration(ldapConfiguration);
        this.ldapConnectionTemplate = new LdapConnectionTemplate(getLdapConnectionPool(connectionConfiguration));
    }

    protected ApacheDsLdapClient(LdapConfiguration ldapConfiguration, LdapConnectionTemplate ldapConnectionTemplate) {
        this.ldapConfiguration = ldapConfiguration;
        this.ldapConnectionTemplate = ldapConnectionTemplate;
    }

    public <T> T authenticate(String username, String password, Mapper<T> mapper) {
        Entry entry = findLdapEntryForAuthentication(username);

        try {
            final PasswordWarning warning = performBind(entry.getDn(), password);

            if (warning != null) {
                LOG.warn("Your password will expire in {} seconds", warning.getTimeBeforeExpiration());
                LOG.warn("Remaining authentications before the account will be locked - {}", warning.getGraceAuthNsRemaining());
                LOG.warn("Password reset is required - {}", warning.isChangeAfterReset());
            }

            return mapper.map(entry);
        } catch (Exception e) {
            throw new cd.go.authentication.ldap.exception.LdapException(String.format("Failed to authenticate user `%s` with ldap server %s", username, ldapConfiguration.getLdapUrlAsString()));
        }
    }

    private PasswordWarning performBind(Dn userDn, String password) throws PasswordException {
        final LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        final LdapConnectionConfig connectionConfig = connectionConfiguration.toLdapConnectionConfig(userDn.getName(), password);
        final BindRequest bindRequest = new BindRequestImpl()
                .setName(userDn.getName())
                .setCredentials(password)
                .addControl(new PasswordPolicyRequestImpl());

        LOG.debug("Performing bind using userDn `{}`.", userDn.getName());
        return new AbstractPasswordPolicyResponder(ldapApiService) {
        }.process(() -> {
            try (LdapNetworkConnection ldapNetworkConnection = new LdapNetworkConnection(connectionConfig)) {
                return ldapNetworkConnection.bind(bindRequest);
            } catch (LdapException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T> List<T> search(final String filter, final String[] filterArgs, final Mapper<T> mapper, final int maxResultCount) {
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
                        .setFilter(FilterEncoder.format(filter, filterArgs))
                        .setTimeLimit(ldapConfiguration.getSearchTimeout())
                        .setBase(new Dn(searchBase));

                searchResults.addAll(ldapConnectionTemplate.search(searchRequest, mapper));
            } catch (LdapException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return searchResults;
    }

    @Override
    public void validate() {
        final String filter = FilterEncoder.format(ldapConfiguration.getUserSearchFilter(), "test");
        ldapConnectionTemplate.searchFirst(ldapConfiguration.getSearchBases().get(0), filter, SearchScope.SUBTREE, entry -> entry);
    }

    public List<Entry> search(final String filter, final String[] filterArgs, final int maxResultCount) {
        return search(filter, filterArgs, resultWrapper -> (Entry) resultWrapper.getResult(), maxResultCount);
    }

    public <T> List<T> searchGroup(List<String> searchBases, String filter, Mapper<T> mapper) {
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

    private int resultsToFetch(final int maxResultCount, final int resultCount) {
        return maxResultCount == 0 ? 0 : maxResultCount > resultCount ? maxResultCount - resultCount : -1;
    }

    private Entry findLdapEntryForAuthentication(String username) {
        final List<Entry> results = search(ldapConfiguration.getUserLoginFilter(), new String[]{username}, resultWrapper -> (Entry) resultWrapper.getResult(), 0);

        if (results.isEmpty()) {
            throw new RuntimeException(String.format("User %s does not exist in %s", username, ldapConfiguration.getLdapUrlAsString()));
        }

        if (results.size() > 1) {
            throw new MultipleUserDetectedException(username, ldapConfiguration.getSearchBases().toString(), ldapConfiguration.getUserLoginFilter());
        }

        return results.get(0);
    }
}
