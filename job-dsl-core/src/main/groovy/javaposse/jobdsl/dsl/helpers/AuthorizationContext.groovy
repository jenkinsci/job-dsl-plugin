package javaposse.jobdsl.dsl.helpers

/**
 * Builds up perms in a closure. So that it be used to build a withXml block
 */
class AuthorizationContext implements Context {
    List<String> perms = []

    AuthorizationContext() {
    }

    AuthorizationContext(List<String> perms) {
        this.perms = perms
    }

    private addAuthorization(String perm) {
        perms << perm
    }

    def permissionAll(String user) {
        Permissions.values().each {
            permission(it, user)
        }
    }

    def permission(String perm) {
        // TODO Check formatting, e.g. has colon
        addAuthorization(perm)
    }

    def permission(Permissions perm, String user) {
        addAuthorization("${perm.longForm}:${user}")
    }

    def permission(String permEnumName, String user) {
        permission(Permissions.valueOf(Permissions, permEnumName), user)
    }
}
