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

package cd.go.authorization.ldap;

import cd.go.framework.ldap.LdapSearch;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cd.go.authorization.ldap.LdapPlugin.LOG;

public class LdapInstances {
    private Map<String, LdapSearch> ldapSearchMap;
    private Map<String, PluginConfiguration> pluginConfigurationMap;

    public LdapInstances() {
        ldapSearchMap = new ConcurrentHashMap<>(5);
        pluginConfigurationMap = new ConcurrentHashMap<>(5);
    }

    public void createLdapSearchInstances(Map<String, PluginConfiguration> pluginConfigurations) {
        ldapSearchMap.clear();
        for (Map.Entry<String, PluginConfiguration> entry : pluginConfigurations.entrySet()) {
            String profileId = entry.getKey();
            try {
                PluginConfiguration pluginConfiguration = entry.getValue();
                LdapSearch ldapSearch = LdapSearch.getInstance(pluginConfiguration);
                ldapSearchMap.put(profileId, ldapSearch);
                pluginConfigurationMap.put(profileId, pluginConfiguration);
            } catch (Exception e) {
                LOG.error("Plugin configuration with profileId " + profileId + " is invalid.", e);
            }
        }
    }

    public Collection<String> getPluginProfile() {
        return ldapSearchMap.keySet();
    }

    public LdapSearch getLdapSearchInstance(String profileId) {
        return ldapSearchMap.get(profileId);
    }

    public PluginConfiguration getPluginConfiguration(String profileId) {
        return pluginConfigurationMap.get(profileId);
    }
}
