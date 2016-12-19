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

/**
 * Enumerable that represents one of the messages that the plugin sends to the server
 */
public enum GoRequest {

    GO_REQUEST_AUTHENTICATE_USER("go.processor.authentication.authenticate-user");

    private final String requestName;

    GoRequest(String requestName) {
        this.requestName = requestName;
    }

    public static GoRequest fromString(String requestName) {
        if (requestName != null) {
            for (GoRequest request : GoRequest.values()) {
                if (requestName.equalsIgnoreCase(request.requestName)) {
                    return request;
                }
            }
        }

        return null;
    }

    public String requestName() {
        return requestName;
    }
}
