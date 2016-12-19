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
 * Enumerable that represents one of the messages that the server sends to the plugin
 */
public enum Request {

    REQUEST_GET_PLUGIN_ICON(Constants.AUTHORIZATION_REQUEST_PREFIX + ".get-icon"),
    REQUEST_GET_CAPABILITIES(Constants.AUTHORIZATION_REQUEST_PREFIX + ".get-capabilities"),
    REQUEST_GET_PLUGIN_CONFIG_METADATA(Constants.AUTHORIZATION_REQUEST_PREFIX + ".get-config-metadata"),
    REQUEST_PLUGIN_CONFIG_VIEW(Constants.AUTHORIZATION_REQUEST_PREFIX + ".get-config-view"),
    REQUEST_VALIDATE_PLUGIN_CONFIG(Constants.AUTHORIZATION_REQUEST_PREFIX + ".validate-plugin-config"),
    REQUEST_VERIFY_CONNECTION(Constants.AUTHORIZATION_REQUEST_PREFIX + ".verify-connection"),

    REQUEST_AUTHENTICATE_USER(Constants.AUTHORIZATION_REQUEST_PREFIX + ".authenticate-user"),
    REQUEST_SEARCH_USERS(Constants.AUTHORIZATION_REQUEST_PREFIX + ".search-users"),

    REQUEST_PLUGIN_CONFIGURATION_FROM_SERVER(Constants.AUTHORIZATION_PROCESSOR_PREFIX + ".get-plugin-config");

//    REQUEST_GET_PROFILE_METADATA(Constants.AUTHENTICATION_CONFIGURATION_REQUEST_PREFIX + ".get-profile-metadata"),
//    REQUEST_GET_PROFILE_VIEW(Constants.AUTHENTICATION_CONFIGURATION_REQUEST_PREFIX + ".get-profile-view"),
//    REQUEST_VALIDATE_PROFILE(Constants.AUTHENTICATION_CONFIGURATION_REQUEST_PREFIX + ".validate-profile");


    private final String requestName;

    Request(String requestName) {
        this.requestName = requestName;
    }

    public static Request fromString(String requestName) {
        if (requestName != null) {
            for (Request request : Request.values()) {
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

    private static class Constants {
        public static final String AUTHORIZATION_REQUEST_PREFIX = "go.cd.authorization";
        public static final String AUTHORIZATION_PROCESSOR_PREFIX = "go.processor.authorization";
    }
}

