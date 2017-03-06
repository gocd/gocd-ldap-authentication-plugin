package cd.go.authentication.ldap.annotation;

public interface Metadata {
    boolean isRequired();

    boolean isSecure();

    FieldType getType();
}
