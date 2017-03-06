package cd.go.framework.ldap.mapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public abstract class AbstractMapper<T> {
    public abstract T mapFromResult(Attributes attributes) throws NamingException;
}
