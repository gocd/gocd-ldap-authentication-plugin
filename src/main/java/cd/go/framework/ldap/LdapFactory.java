package cd.go.framework.ldap;

import cd.go.authentication.ldap.model.LdapConfiguration;

public class LdapFactory {
    public Ldap ldapForConfiguration(LdapConfiguration configuration) {
        return new Ldap(configuration);
    }
}
