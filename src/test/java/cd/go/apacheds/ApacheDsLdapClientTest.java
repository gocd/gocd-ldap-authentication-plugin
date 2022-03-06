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
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.filter.FilterParser;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.template.EntryMapper;
import org.apache.directory.ldap.client.template.LdapConnectionTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ApacheDsLdapClientTest {

    @Test
    void shouldBeAbleToSearchUsers() throws ParseException {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .withSearchTimeout(10)
                .withSearchBases("ou=foo,dc=bar")
                .build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);
        final ArgumentCaptor<SearchRequest> argumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        when(ldapConnectionTemplate.search(argumentCaptor.capture(), ArgumentMatchers.<EntryMapper<Entry>>any())).thenReturn(Collections.singletonList(new DefaultEntry()));

        ldap.search("(uid={0})", new String[]{"foo"}, 1);

        final SearchRequest searchRequest = argumentCaptor.getValue();

        assertThat(searchRequest.getBase()).isEqualTo("ou=foo,dc=bar");
        assertThat(searchRequest.getScope()).isEqualTo(SearchScope.SUBTREE);
        assertThat(searchRequest.getAttributes()).isEqualTo(Collections.singletonList("*"));
        assertThat(searchRequest.getSizeLimit()).isEqualTo(1L);
        assertThat(searchRequest.getFilter()).isEqualTo(FilterParser.parse("(uid=foo)"));
        assertThat(searchRequest.getTimeLimit()).isEqualTo(10);
    }

    @Test
    void shouldEscapeSearchFilterValues() throws ParseException {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .withSearchTimeout(10)
                .withSearchBases("ou=foo,dc=bar")
                .build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);
        final ArgumentCaptor<SearchRequest> argumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        when(ldapConnectionTemplate.search(argumentCaptor.capture(), ArgumentMatchers.<EntryMapper<Entry>>any())).thenReturn(Collections.singletonList(new DefaultEntry()));

        String injectionUserName = "*)(objectclass=*";
        ldap.search("(uid={0})", new String[]{injectionUserName}, 1);

        assertThat(argumentCaptor.getValue().getFilter())
                .isEqualTo(FilterParser.parse("(uid=\\2A\\29\\28objectclass=\\2A)"));
    }

    @Test
    void shouldAbleToFetchResultsFromMultipleSearchBase() {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .withSearchBases("ou=foo,dc=bar", "ou=baz,dc=bar")
                .build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);
        final ArgumentCaptor<SearchRequest> argumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        when(ldapConnectionTemplate.search(argumentCaptor.capture(), ArgumentMatchers.<EntryMapper<Entry>>any()))
                .thenReturn(Collections.singletonList(new DefaultEntry()))
                .thenReturn(Collections.singletonList(new DefaultEntry()));


        final List<Entry> entries = ldap.search("(uid={0})", new String[]{"foo"}, 0);

        final List<SearchRequest> searchRequests = argumentCaptor.getAllValues();

        assertThat(entries).hasSize(2);
        assertThat(searchRequests.get(0).getBase()).isEqualTo("ou=foo,dc=bar");
        assertThat(searchRequests.get(1).getBase()).isEqualTo("ou=baz,dc=bar");
    }

    @Test
    void shouldStopSearchingIfMaxResultLimitReached() {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .withSearchBases("ou=foo,dc=bar", "ou=baz,dc=bar")
                .build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);
        final ArgumentCaptor<SearchRequest> argumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        when(ldapConnectionTemplate.search(argumentCaptor.capture(), ArgumentMatchers.<EntryMapper<Entry>>any()))
                .thenReturn(Arrays.asList(new DefaultEntry()));

        final List<Entry> entries = ldap.search("(uid={0})", new String[]{"foo"}, 1);

        final List<SearchRequest> searchRequests = argumentCaptor.getAllValues();

        assertThat(entries).hasSize(1);
        assertThat(searchRequests).hasSize(1);
        assertThat(searchRequests.get(0).getBase()).isEqualTo("ou=foo,dc=bar");
    }

    @Test
    void shouldSearchGroupsBasedOnGroupMembershipFilter() throws Exception {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);
        final ArgumentCaptor<SearchRequest> argumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        when(ldapConnectionTemplate.search(argumentCaptor.capture(), ArgumentMatchers.<EntryMapper<Entry>>any())).thenReturn(Collections.singletonList(new DefaultEntry()));

        ldap.searchGroup(Arrays.asList("ou=foo,dc=bar"), "(member=admin)", entry -> entry);

        final SearchRequest searchRequest = argumentCaptor.getValue();
        assertThat(searchRequest.getBase()).isEqualTo("ou=foo,dc=bar");
        assertThat(searchRequest.getScope()).isEqualTo(SearchScope.SUBTREE);
        assertThat(searchRequest.getAttributes()).isEqualTo(Collections.singletonList("dn"));
        assertThat(searchRequest.getSizeLimit()).isEqualTo(0L);
        assertThat(searchRequest.getFilter()).isEqualTo(FilterParser.parse("(member=admin)"));
        assertThat(searchRequest.getTimeLimit()).isEqualTo(5);
    }

    @Test
    void searchGroups_shouldAbleToFetchResultsFromMultipleSearchBase() {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder().build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);
        final ArgumentCaptor<SearchRequest> argumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        when(ldapConnectionTemplate.search(argumentCaptor.capture(), ArgumentMatchers.<EntryMapper<Entry>>any()))
                .thenReturn(Collections.singletonList(new DefaultEntry()))
                .thenReturn(Collections.singletonList(new DefaultEntry()));


        final List<Entry> entries = ldap.searchGroup(Arrays.asList("ou=foo,dc=bar", "ou=baz,dc=bar"), "(member=admin)", resultWrapper -> (Entry) resultWrapper.getResult());

        final List<SearchRequest> searchRequests = argumentCaptor.getAllValues();

        assertThat(entries).hasSize(2);
        assertThat(searchRequests.get(0).getBase()).isEqualTo("ou=foo,dc=bar");
        assertThat(searchRequests.get(1).getBase()).isEqualTo("ou=baz,dc=bar");
    }

    @Test
    void shouldVerifyConnectionByMakingADummySearchRequest() {
        final LdapConfiguration ldapConfiguration = new LdapConfigurationBuilder()
                .withSearchBases("ou=foo,dc=bar").build();

        final LdapConnectionTemplate ldapConnectionTemplate = mock(LdapConnectionTemplate.class);
        final ApacheDsLdapClient ldap = new ApacheDsLdapClient(ldapConfiguration, ldapConnectionTemplate);

        ldap.validate();

        verify(ldapConnectionTemplate).searchFirst(
                eq("ou=foo,dc=bar"),
                eq("(|(sAMAccountName=*test*)(uid=*test*)(cn=*test*)(mail=*test*)(otherMailbox=*test*))"),
                eq(SearchScope.SUBTREE),
                ArgumentMatchers.<EntryMapper<Entry>>any()
        );
    }
}