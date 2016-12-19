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

import cd.go.authorization.ldap.models.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.naming.NamingException;
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

        assertThat(user, is(new User("jduke", "Java Duke", "jduke@example.com")));
    }

    @Test
    public void shouldBarfWhenMappingFromInvalidAttributes() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("uid", "jduke");
        attributes.put("displayName", "Java Duke");
        UserMapper userMapper = new UserMapper("uid", "displayName", "mail");

        thrown.expect(any(NamingException.class));
        thrown.expectMessage("Failed to get attribute `mail` value.");
        User user = userMapper.mapFromResult(attributes);

        assertNull(user);
    }
}