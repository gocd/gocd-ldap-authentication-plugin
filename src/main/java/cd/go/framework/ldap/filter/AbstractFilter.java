/*
 * Copyright 2005-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.framework.ldap.filter;

public abstract class AbstractFilter implements Filter {

    private static final int DEFAULT_BUFFER_SIZE = 256;

    @Override
    public String prepare() {
        StringBuffer buf = new StringBuffer(DEFAULT_BUFFER_SIZE);
        buf = prepare(buf);
        return buf.toString();
    }

}
