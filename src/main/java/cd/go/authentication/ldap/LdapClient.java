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

package cd.go.authentication.ldap;

import cd.go.authentication.ldap.mapper.Mapper;

import javax.naming.NamingException;
import java.util.List;

public interface LdapClient {
    <T> T authenticate(String username, String password, Mapper<T> mapper);

    <T> List<T> search(String userSearchFilter, String[] filterArgs, Mapper<T> mapper, int maxResult);

    void validate() throws NamingException;
}
