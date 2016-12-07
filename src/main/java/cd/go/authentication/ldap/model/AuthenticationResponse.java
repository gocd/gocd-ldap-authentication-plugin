package cd.go.authentication.ldap.model;

public class AuthenticationResponse {
    private User user;
    private String profileId;

    public AuthenticationResponse(User user, String profileId) {
        this.user = user;
        this.profileId = profileId;
    }

    public User getUser() {
        return user;
    }
}
