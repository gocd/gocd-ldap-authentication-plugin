package cd.go.authentication.ldap;

import cd.go.authentication.ldap.mapper.AttributesMapper;
import cd.go.authentication.ldap.model.Authentication;
import cd.go.authentication.ldap.model.AuthenticationResponse;
import cd.go.authentication.ldap.model.LdapProfile;
import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.Ldap;

import javax.naming.directory.Attributes;
import java.util.Map;

import static cd.go.authentication.ldap.LdapAuthenticationPlugin.LOG;

public class LdapAuthenticator {

    public AuthenticationResponse authenticate(Authentication authentication, Map<String, LdapProfile> ldapProfiles) {
        for (Map.Entry<String, LdapProfile> entry : ldapProfiles.entrySet()) {
            AuthenticationResponse authenticationResponse = authenticateWithProfile(authentication, entry.getKey(), entry.getValue());
            if (authenticationResponse != null)
                return authenticationResponse;
        }
        return null;
    }

    private AuthenticationResponse authenticateWithProfile(Authentication authentication, String profileId, LdapProfile profile) {
        try {
            Attributes attributes = new Ldap(profile).authenticate(authentication.getUsername(), authentication.getPassword(), new AttributesMapper());
            User user = profile.getUserMapper().mapFromResult(attributes);
            if (user != null) {
                LOG.info("User `" + user.getUsername() + "` successfully authenticated using " + profileId);
                return new AuthenticationResponse(user, profileId);
            }
        } catch (Exception e) {
            LOG.error("", e);
            LOG.error("Failed to authenticate user " + authentication.getUsername() + " on " + profile.getLdapUrl() + ". " + e.getMessage());
        }
        return null;
    }
}

