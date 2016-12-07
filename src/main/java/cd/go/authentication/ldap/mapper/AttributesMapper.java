package cd.go.authentication.ldap.mapper;

import cd.go.framework.ldap.mapper.AbstractMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class AttributesMapper extends AbstractMapper<Attributes> {
    @Override
    public Attributes mapFromResult(Attributes attributes) throws NamingException {
        return attributes;
    }
}
