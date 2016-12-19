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

import com.thoughtworks.go.plugin.api.response.GoApiResponse;

import static java.lang.String.format;

public class ServerRequestFailedException extends Exception {

    private ServerRequestFailedException(GoApiResponse response, String request) {
        super(format(
                "The server sent an unexpected status code %d with the response body %s when it was sent a %s message",
                response.responseCode(), response.responseBody(), request
        ));
    }

    public static ServerRequestFailedException getPluginSettings(GoApiResponse response) {
        return new ServerRequestFailedException(response, "get plugin settings");
    }
}
