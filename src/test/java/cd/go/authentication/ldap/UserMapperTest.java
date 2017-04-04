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
import cd.go.authentication.ldap.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class UserMapperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldAbleToMapUserFromValidAttributes() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("uid", "jduke");
        attributes.put("displayName", "Java Duke");
        attributes.put("mail", "jduke@example.com");

        UserMapper userMapper = new UserMapper("uid", "displayName", "mail");
        User user = userMapper.mapFromResult(attributes);

        assertThat(user, is(new User("jduke", "Java Duke", "jduke@example.com", null)));
    }

    @Test
    public void shouldBarfWhenMappingUsernameFromInvalidAttributes() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("displayName", "Java Duke");
        UserMapper userMapper = new UserMapper("non-exiting-field", "displayName", "mail");

        thrown.expect(any(InvalidUsernameException.class));
        thrown.expectMessage("Username can not be blank. Please check `SearchFilter` attribute on `<authConfig>` profile.");
        User user = userMapper.mapFromResult(attributes);

        assertNull(user);
    }
}