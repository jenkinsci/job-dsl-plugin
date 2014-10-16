package javaposse.jobdsl.dsl.helpers

/**
 * Builds up perms in a closure. So that it be used to build a withXml block
 */
class AuthorizationContext implements Context {
    List<String> permissions = []

    def permissionAll(String user) {
        Permissions.values().each {
            permission(it, user)
        }
    }

    def permission(String permission) {
        permissions << permission
    }

    def permission(Permissions permission, String user) {
        permissions << "${permission.longForm}:${user}"
    }

    def permission(String permissionEnumName, String user) {
        permission(Permissions.valueOf(permissionEnumName), user)
    }
}
