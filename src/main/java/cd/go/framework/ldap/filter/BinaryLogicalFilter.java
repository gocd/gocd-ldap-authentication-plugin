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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class BinaryLogicalFilter extends AbstractFilter {

    private List<Filter> queryList = new LinkedList<Filter>();

    public StringBuffer prepare(StringBuffer buffer) {
        if (queryList.size() <= 0) {
            return buffer;
        } else if (queryList.size() == 1) {
            Filter query = queryList.get(0);
            return query.prepare(buffer);
        } else {
            buffer.append("(").append(getLogicalOperator());

            for (Filter query : queryList) {
                query.prepare(buffer);
            }

            buffer.append(")");
            return buffer;
        }
    }

    protected abstract String getLogicalOperator();

    public final BinaryLogicalFilter append(Filter query) {
        queryList.add(query);
        return this;
    }

    public final BinaryLogicalFilter appendAll(Collection<Filter> subQueries) {
        queryList.addAll(subQueries);
        return this;
    }
}
