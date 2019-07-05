/*
 * Copyright 2017 ThoughtWorks, Inc.
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

import cd.go.authentication.ldap.exception.InvalidUsernameException;
import cd.go.authentication.ldap.mapper.UserMapper;
import cd.go.authentication.ldap.mapper.UsernameResolver;
import cd.go.authentication.ldap.model.User;
import org.junit.jupiter.api.Test;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UserMapperTest {
    @Test
    void shouldAbleToMapUserFromValidAttributes() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("uid", "jduke");
        attributes.put("displayName", "Java Duke");
        attributes.put("mail", "jduke@example.com");

        UserMapper userMapper = new UserMapper(new UsernameResolver(), "displayName", "mail");
        User user = userMapper.mapFromResult(attributes);

        assertThat(user).isEqualTo(new User("jduke", "Java Duke", "jduke@example.com", null));
    }

    @Test
    void shouldAbleToMapUserWithGivenUsername() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("uid", "jduke");
        attributes.put("displayName", "Java Duke");
        attributes.put("mail", "jduke@example.com");

        UserMapper userMapper = new UserMapper(new UsernameResolver("J Dude"), "displayName", "mail");
        User user = userMapper.mapFromResult(attributes);

        assertThat(user).isEqualTo(new User("J Dude", "Java Duke", "jduke@example.com", null));
    }

    @Test
    void shouldBarfWhenMappingUsernameFromInvalidAttributes() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("displayName", "Java Duke");
        UserMapper userMapper = new UserMapper(new UsernameResolver(), "displayName", "mail");

        assertThatCode(() -> userMapper.mapFromResult(attributes))
                .isInstanceOf(InvalidUsernameException.class)
                .hasMessage("Username can not be blank. Failed to resolve username using the attributes `sAMAccountName` and `uid`.");
    }
}