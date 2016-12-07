package cd.go.authentication.ldap.mapper;

import cd.go.authentication.ldap.model.User;
import cd.go.framework.ldap.mapper.AbstractMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import static cd.go.authentication.ldap.LdapAuthenticationPlugin.LOG;

public class UserMapper extends AbstractMapper<User> {
    private final String usernameAttribute;
    private final String displayNameAttribute;
    private final String emailAttribute;

    public UserMapper(String usernameAttribute, String displayNameAttribute, String emailAttribute) {
        this.usernameAttribute = usernameAttribute;
        this.displayNameAttribute = displayNameAttribute;
        this.emailAttribute = emailAttribute;
    }

    @Override
    public User mapFromResult(Attributes attributes) throws NamingException {
        User user = new User(resolveAttribute(usernameAttribute, attributes),
                resolveAttribute(displayNameAttribute, attributes),
                resolveAttribute(emailAttribute, attributes));

        return user;
    }

    private String resolveAttribute(String attributeName, Attributes attributes) {
        try {
            Attribute attribute = attributes.get(attributeName);
            return attribute.get().toString();
        } catch (NullPointerException | NamingException e) {
            LOG.error("Failed to get attribute `" + attributeName + "` value.");
        }
        return null;
    }
}
