package cd.go.framework.ldap;

import cd.go.authentication.ldap.model.LdapProfile;

import javax.naming.Context;
import java.util.Hashtable;

public class Environment {

    private static final String AUTHENTICATION_TYPE = "simple";
    private static final String LDAP_LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String LDAP_CONNECT_POOL_MAXSIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
    private static final String LDAP_CONNECT_POOL_PREFSIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
    private static final String LDAP_CONNECT_POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
    private static final String LDAP_CONNECT_POOL = "com.sun.jndi.ldap.connect.pool";

    private LdapProfile ldapProfile;
    private boolean useConnectionPool;

    public Environment(LdapProfile ldapProfile, boolean useConnectionPool) {
        this.ldapProfile = ldapProfile;
        this.useConnectionPool = useConnectionPool;
    }

    public Hashtable getEnvironments() {
        Hashtable environments = new Hashtable(10);
        environments.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_LDAP_CTX_FACTORY);
        environments.put(Context.PROVIDER_URL, ldapProfile.getLdapUrl());
        environments.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION_TYPE);

        if (useConnectionPool) {
            environments.putAll(getConnectionPoolEnvironments());
        }

        return environments;
    }

    public Hashtable getConnectionPoolEnvironments() {
        Hashtable env = new Hashtable(4);
        env.put(LDAP_CONNECT_POOL, "true");
        env.put(LDAP_CONNECT_POOL_MAXSIZE, ldapProfile.getLdapConnectionPoolSize());
        env.put(LDAP_CONNECT_POOL_PREFSIZE, ldapProfile.getLdapConnectionPoolPrefSize());
        env.put(LDAP_CONNECT_POOL_TIMEOUT, ldapProfile.getLdapConnectionPoolTimeout());
        return env;
    }
}
