package cd.go.authentication.ldap.exception;

import com.thoughtworks.go.plugin.api.response.GoApiResponse;

import static java.lang.String.format;

public class ServerRequestFailedException extends Exception {

    private ServerRequestFailedException(GoApiResponse response, String request) {
        super(format(
                "The server sent an unexpected status code %d with the response body %s when it was sent a %s message",
                response.responseCode(), response.responseBody(), request
        ));
    }

    public static ServerRequestFailedException getLdapProfiles(GoApiResponse response) {
        return new ServerRequestFailedException(response, "get ldap profiles");
    }
}
