package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Strings
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

import static com.google.common.base.Preconditions.checkArgument

/**
 * Builds up perms in a closure. So that it be used to build a withXml block
 */
class AuthorizationContext implements Context {
    private final JobManagement jobManagement
    Set<String> permissions = []

    AuthorizationContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void permissionAll(String user) {
        availablePermissions.each {
            addPermission(it, user)
        }
    }

    void permission(String permission) {
        checkArgument(!Strings.isNullOrEmpty(permission), 'permission must not be null or empty')
        checkArgument(permission.contains(':'), 'permission must be <permission>:<user>')

        String[] permissionAndUser = permission.split(':', 2)
        this.permission(permissionAndUser[0], permissionAndUser[1])
    }

    @Deprecated
    void permission(Permissions permission, String user) {
        jobManagement.logDeprecationWarning()
        addPermission(permission.longForm, user)
    }

    void permission(String permissionEnumName, String user) {
        String permission = getPermissionFromEnumValue(permissionEnumName)
        checkArgument(
                availablePermissions.contains(permission),
                "permission must be one of ${availablePermissions.join(',')}"
        )
        addPermission(permission, user)
    }

    private void addPermission(String permission, String user) {
        permissions << "${permission}:${user}".toString()
    }

    private String getPermissionFromEnumValue(String permissionOrEnumValue) {
        String permission
        try {
            permission = Permissions.valueOf(permissionOrEnumValue).longForm
            jobManagement.logDeprecationWarning('using the permission enum values')
        } catch (IllegalArgumentException ignore) {
            permission = permissionOrEnumValue
        }
        permission
    }

    private Set<String> getAvailablePermissions() {
        jobManagement.getPermissions('hudson.security.AuthorizationMatrixProperty')
    }
}
