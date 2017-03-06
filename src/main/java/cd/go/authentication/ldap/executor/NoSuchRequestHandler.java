package cd.go.authentication.ldap.executor;

public class NoSuchRequestHandler extends RuntimeException {
    public NoSuchRequestHandler(String message) {
        super(message);
    }
}
