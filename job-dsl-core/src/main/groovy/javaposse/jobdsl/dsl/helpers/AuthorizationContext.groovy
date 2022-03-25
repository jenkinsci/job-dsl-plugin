package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

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

    /**
     * Adds all available permissions for the user.
     */
    void permissionAll(String type, String user) {
        availablePermissions.each {
            addPermission(type, it, user)
        }
    }

    /**
     * Adds a specific permission.
     *
     * @param permission a permission string in {@code '<type>:<permission>:<user>'} format, e.g.
     * {@code 'hudson.model.Item.Create:authenticated'}
     */
    void permission(String permission) {
        checkNotNullOrEmpty(permission, 'permission must not be null or empty')
        checkArgument(permission.contains(':'), 'permission must be <type>:<permission>:<user>')

        String[] permissionAndUser = permission.split(':', 3)
        this.permission(permissionAndUser[0], permissionAndUser[1], permissionAndUser[2])
    }

    /**
     * Adds a specific permission, but breaks apart the permission from the user name.
     */
    void permission(String type, String permission, String user) {
        checkArgument(
                availablePermissions.contains(permission),
                "permission must be one of ${availablePermissions.join(',')}"
        )
        checkArgument(
                ["USER", "GROUP"].contains(type),
                "type must be USER or GROUP"
        )
        addPermission(type, permission, user)
    }

    /**
     * Adds a set of permissions for a user.
     *
     * @since 1.66
     */
    void permissions(String type, String user, Iterable<String> permissionsList) {
        permissionsList.each {
            this.permission(type, it, user)
        }
    }

    protected void addPermission(String type, String permission, String user) {
        permissions << "${type}:${permission}:${user}".toString()
    }

    protected Set<String> getAvailablePermissions() {
        jobManagement.getPermissions(authorizationMatrixPropertyClassName)
    }
}
