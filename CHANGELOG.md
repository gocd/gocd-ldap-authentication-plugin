# Changelog

## 2.0.0

* [#44](https://github.com/gocd/gocd-ldap-authentication-plugin/pull/44) Implement authorization extension v2 to support:
  - 'get-user-roles' capabilities
  - 'is-valid-user' authorization extension request

## 1.0.3

- [#43](https://github.com/gocd/gocd-ldap-authentication-plugin/pull/43) - Cleaner logs by default

  - Logs won't show stack traces by default for a failed authentication. Can turn on DEBUG logging to see stack traces.

## 1.0.2

- [#39](https://github.com/gocd/gocd-ldap-authentication-plugin/pull/39) - Deny authentication when multiple user's matches to the username using UserLoginFilter in a search base

## 1.0.1

- Cleanup the build script. No changes in functionality

## 1.0.0

### Improved

Reduced the size of the plugin from 1.1MB to 270KB by removing dependencies on utility code from apache commons. There are no functional changes from the previous release.

## [3601af8](https://github.com/gocd/gocd-ldap-authentication-plugin/commit/3601af806a2781ca679cc0f5dae485a37319818a) - Bundled with *GoCD v17.5.0*


### Changed

1. Authorization Configuration changes
  * `ManagerDN` is non-mandatory, `Password` is required only if a `ManagerDN` is specified.
  * `DisplayNameAttribute` is optional and defaulted to `cn`.
  * `EmailAttribute` is optional and defaulted to 'mail'.
  * `LoginAttribute` changed to `UserLoginFilter` which is an LDAP search filter used during authentication to lookup for a user entry matching the given expression.
  * `SearchAttribute` changed to `UserSearchFilter` which is an LDAP search filter used to lookup for users matching a given search term.
2. Verify Connection validates the auth config before checking connection.

### Fixed

1. When multiple search bases are defined, a invalid search base should not error out the operations. Errors are logged and the operation continues with the next search base.


### [v0.0.1(Experimental)](https://github.com/gocd/gocd-ldap-authentication-plugin/releases/tag/0.0.1)


    Initial release of plugin.
