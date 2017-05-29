# GoCD LDAP/AD Authentication Plugin

LDAP plugin which implements the GoCD [Authorization Plugin](https://plugin-api.gocd.io/current/authorization/) endpoint.

## Building the code base

To build the jar, run `./gradlew clean test assemble`

## Requirements

These plugins require GoCD version v17.5 or above.

## Installation

- From GoCD `17.5.0` onwards the plugin comes bundled along with server, hence a separate installation is not required.

## Configuration

The plugin requires necessary configurations to connect to LDAP/AD. The configuration can be added by adding a Authorization Configuration
by visting the Authorization Configuration page under Admin/Security.

Alternatively, the configuration can be added directly to the `config.xml` using the `<authConfig>` configuration.
  
* Example Configuration
 
   ```xml
    <security>
      <authConfigs>
        <authConfig id="profile-id" pluginId="cd.go.authentication.ldap">
          <property>
            <key>Url</key>
            <value>ldap://ldap-server-url</value>
          </property>
          <property>
            <key>ManagerDN</key>
            <value>cn=go,ou=Teams,dc=corporate,dc=example,dc=com</value>
          </property>
          <property>
            <key>Password</key>
            <value>secret</value>
          </property>
          <property>
            <key>SearchBases</key>
            <value>ou=Teams,dc=corporate,dc=example,dc=com</value>
          </property>
          <property>
            <key>UserLoginFilter</key>
            <value>(sAMAccountName={0})</value>
          </property>
          <property>
            <key>UserSearchFilter</key>
            <value>(|(sAMAccountName=*{0}*)(uid=*{0}*)(cn=*{0}*)(mail=*{0}*)(otherMailbox=*{0}*))</value>
          </property>
          <property>
            <key>DisplayNameAttribute</key>
            <value>displayName</value>
          </property>
          <property>
            <key>EmailAttribute</key>
            <value>mail</value>
          </property>
        </authConfig>
      </authConfigs>
    </security>
    ```
* **Url (Mandatory) :** Specify your ldap server URL. The plugin does not [support](https://github.com/gocd/gocd-ldap-authentication-plugin/issues/24) configuring certificates for connecting to LDAP server over SSL, a workaround for this issue involves importing the certificates directly into java's cacerts.


    ```xml
    <property>
       <key>Url</key>
       <value>ldap://ldap-server-url:1234</value>
    </property>
    ```

* **ManagerDN (Optional)  :**  The LDAP/AD manager user's DN, used to connect to the LDAP/AD server.
 
    ```xml
    <property>
       <key>ManagerDN</key>
       <value>uid=admin,ou=system,dc=example,dc=com</value>
    </property>
    ```
* **Password (Mandatory if ManagerDN provided) :** The LDAP/AD manager password, used to connect to the LDAP/AD server. Required only if a ManagerDN is specified.
* **SearchBases (Mandatory) :** This field defines the location in the directory from which the LDAP search begins.
You can provide multiple search bases. If multiple search bases are configured the plugin would look for the user in each search base sequentially
until the user is found.

    > Single search base: 
    ```xml
    <property>
       <key>SearchBases</key>
       <value>ou=users,ou=system</value>
    </property>
    ```
    
    > Multiple search base
    ```xml
    <property>
       <key>SearchBases</key>
       <value>
        ou=users,ou=system
        ou=employee,ou=system
        </value>
    </property>
    ```
* **UserLoginFilter (Mandatory) :** It is an LDAP search filter used during authentication to lookup for a user entry matching the given expression.
In the below example the filter would search for a username matching the ```sAMAccountName``` attribute.
    
    ```xml
    <property>
       <key>UserLoginFilter</key>
       <value>(sAMAccountName={0})</value>
    </property>
    ```
    
* **UserSearchFilter (Optional) :** It is an LDAP search filter used to lookup for users matching a given search term.
This is an optional configuration, the default filter used is ```(|(sAMAccountName=*{0}*)(uid=*{0}*)(cn=*{0}*)(mail=*{0}*)(otherMailbox=*{0}*))```.
    ```xml
    <property>
       <key>UserSearchFilter</key>
       <value>(|(sAMAccountName=*{0}*)(uid=*{0}*))</value>
    </property>
    ```
  
* **DisplayNameAttribute (Optional) :** Value of this attribute is mapped to GoCD User displayname, default attribute used is ```cn```.

    ```xml
    <property>
       <key>DisplayNameAttribute</key>
       <value>displayName</value>
    </property>
    ```

* **EmailAttribute (Optional) :** Value of this attribute is mapped to GoCD User mail, default value used is ```mail```.
 
   ```xml
    <property>
        <key>EmailAttribute</key>
        <value>mail</value>
    </property>
    ```

*Note: The plugin allows having multiple configurations to connect to different LDAP/AD servers*
```xml
<authConfig id="second-profile-id" pluginId="cd.go.authentication.ldap">
...
</authConfig>
```

## License

```plain
Copyright 2017 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
