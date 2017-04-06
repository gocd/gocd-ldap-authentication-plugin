# GoCD LDAP Authentication Plugin
GoCD plugin for LDAP authentication

## Building the code base
To build the jar, run `./gradlew clean test assemble`

## Requirements
These plugins require GoCD version v17.2 or above.

## Installation
- Download the latest plugin jar from the [Releases](https://github.com/gocd/gocd-ldap-authentication-plugin/releases) section. Place it in `<go-server-location>/plugins/external` & restart Go Server. You can find the location of the Go Server installation [here](http://www.go.cd/documentation/user/current/installation/installing_go_server.html#location-of-files-after-installation-of-go-server).
- Developer
    * [Building the code base](https://github.com/gocd/gocd-ldap-authentication-plugin/blob/master/README.md#building-the-code-base)
    * Build path: `<project-location>/build/libs/<your-jar-is-here>`

## Configuration
This plugin uses `<authConfig>` profile(s) in order to connect with `ldap server` which you have to configure in `config.xml` under `<security>` configuration.
  
* Example profile
 
```xml
    <authConfigs>
      <authConfig id="profile-id" pluginId="cd.go.authentication.ldap">
        <property>
          <key>Url</key>
          <value>ldap://ldap-server-url</value>
        </property>
        <property>
          <key>ManagerDN</key>
          <value>your-manager-dn</value>
        </property>
        <property>
          <key>Password</key>
          <value>manager-password</value>
        </property>
        <property>
          <key>SearchBases</key>
          <value>your-user-search-base</value>
        </property>
        <property>
          <key>LoginAttribute</key>
          <value>login-attribute</value>
        </property>
        <property>
          <key>SearchAttributes</key>
          <value>search-attributes</value>
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
```  
* **Url:** Specify your ldap server URL
* **ManagerDN:** Specify full mangerDN
 
    ```xml
    <property>
       <key>ManagerDN</key>
       <value>uid=admin,ou=system,dc=example,dc=com</value>
    </property>
    ```
* **Password:** Specify manager password
* **SearchBases:** Provide user search base of your ldap server. You can provide multiple search bases

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
* **LoginAttribute:** This is a mandatory field which is required to uniquely identity a user in the ldap server.  
    
    ```xml
    <property>
       <key>LoginAttribute</key>
       <value>sAMAccountName</value>
    </property>
    ```
    
* **SearchAttributes:** SearchAttributes can be used to configure user search to look for certain attributes.    
    ```xml
    <property>
       <key>SearchAttributes</key>
       <value>mail, name, sn, l</value>
    </property>
    ```
  
* **DisplayNameAttribute:** Value of this attribute is displayed on GoCD user profile
* **EmailAttribute:** This allows user to map custom `email` field of their LDAP server.
 

*Note: You can also configure multiple LDAP serve by adding* 
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
