package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Strings
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

import static com.google.common.base.Preconditions.checkArgument

/**
 * Builds up perms in a closure. So that it be used to build a withXml block
 */
class AuthorizationContext extends AbstractContext {
    private final String authorizationMatrixPropertyClassName
    Set<String> permissions = new LinkedHashSet<String>()

    AuthorizationContext(JobManagement jobManagement, String authorizationMatrixPropertyClassName) {
        super(jobManagement)
        this.authorizationMatrixPropertyClassName = authorizationMatrixPropertyClassName
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

    void permission(String permission, String user) {
        checkArgument(
                availablePermissions.contains(permission),
                "permission must be one of ${availablePermissions.join(',')}"
        )
        addPermission(permission, user)
    }

    protected void addPermission(String permission, String user) {
        permissions << "${permission}:${user}".toString()
    }

    protected Set<String> getAvailablePermissions() {
        jobManagement.getPermissions(authorizationMatrixPropertyClassName)
    }
}
