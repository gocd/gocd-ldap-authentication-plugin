/*
 * Copyright 2022 Thoughtworks, Inc.
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

package cd.go.framework.ldap;

import java.text.MessageFormat;

public class SearchResultLimitExceededException extends RuntimeException {
    private final int maxResult;
    private final String searchBase;

    public SearchResultLimitExceededException(int maxResult, String searchBase) {
        super(MessageFormat.format("Search results limit exceeded. Expected search to return {0} results but found more in search base {1}.", maxResult, searchBase));
        this.maxResult = maxResult;
        this.searchBase = searchBase;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public String getSearchBase() {
        return searchBase;
    }
}
