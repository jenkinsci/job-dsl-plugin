package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

/**
 * Builds up perms in a closure. So that it be used to build a withXml block
 */
class AuthorizationContext extends AbstractContext {
    private final String authorizationMatrixPropertyClassName
    private final enum AuthType {
      GROUP,
      USER;
    }
    Set<String> permissions = new LinkedHashSet<String>()

    AuthorizationContext(JobManagement jobManagement, String authorizationMatrixPropertyClassName) {
        super(jobManagement)
        this.authorizationMatrixPropertyClassName = authorizationMatrixPropertyClassName
    }

    /**
     * Adds all available permissions for the user.
     */
    void permissionAll(String user) {
        availablePermissions.each {
            addPermission(it, user)
        }
    }

    /**
     * Adds all available permissions for the user.
     *
     * @since 1.88
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '3.0')
    void userPermissionAll(String userName) {
        availablePermissions.each {
            addAuthTypedPermission(AuthType.USER, it, userName)
        }
    }

    /**
     * Adds all available permissions for the group.
     *
     * @since 1.88
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '3.0')
    void groupPermissionAll(String groupName) {
        availablePermissions.each {
            addAuthTypedPermission(AuthType.GROUP, it, groupName)
        }
    }

    /**
     * Adds a specific permission.
     *
     * @param permission a permission string in {@code '[USER:|GROUP:]<permission>:<user>'} format, e.g.
     * {@code '[USER:|GROUP:]hudson.model.Item.Create:authenticated'}
     */
    void permission(String permission) {
        checkNotNullOrEmpty(permission, 'permission must not be null or empty')
        checkArgument(permission.contains(':'), 'permission must be [USER:|GROUP:]<permission>:<user>')

        String[] permissionParts = permission.split(':', 3)
        String prefix = permissionParts[0]
        if (permissionParts.size() == 3) {
          List<String> authTypes = AuthType.values()*.toString()
          checkArgument(authTypes.contains(prefix),
                  "Prefix must be ${authTypes.join(' or ')}")
        }
        if (permissionParts.size() == 2) {
          this.permission(permissionParts[0], permissionParts[1])
        } else if (prefix == AuthType.USER.toString()) {
          jobManagement.requireMinimumPluginVersion('matrix-auth', '3.0')
          this.userPermission(permissionParts[1], permissionParts[2])
        } else if (prefix == AuthType.GROUP.toString()) {
          jobManagement.requireMinimumPluginVersion('matrix-auth', '3.0')
          this.groupPermission(permissionParts[1], permissionParts[2])
        }
    }

    /**
     * Adds a specific permission, but breaks apart the permission from the user name.
     */
    void permission(String permission, String user) {
        checkArgument(
                availablePermissions.contains(permission),
                "permission must be one of ${availablePermissions.join(',')}"
        )
        addPermission(permission, user)
    }

    /**
     * Adds a specific permission, but breaks apart the permission from the user name.
     *
     * @since 1.88
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '3.0')
    void userPermission(String permission, String userName) {
        checkArgument(
                availablePermissions.contains(permission),
                "permission must be one of ${availablePermissions.join(',')}"
        )
        addAuthTypedPermission(AuthType.USER, permission, userName)
    }

    /**
     * Adds a specific permission, but breaks apart the permission from the group name.
     *
     * @since 1.88
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '3.0')
    void groupPermission(String permission, String groupName) {
        checkArgument(
                availablePermissions.contains(permission),
                "permission must be one of ${availablePermissions.join(',')}"
        )
      addAuthTypedPermission(AuthType.GROUP, permission, groupName)
    }

    /**
     * Adds a set of permissions for a user.
     *
     * @since 1.66
     */
    void permissions(String user, Iterable<String> permissionsList) {
        permissionsList.each {
            this.permission(it, user)
        }
    }

    /**
     * Adds a set of permissions for a user.
     *
     * @since 1.88
     */
    void userPermissions(String userName, Iterable<String> permissionsList) {
        permissionsList.each {
            this.addAuthTypedPermission(AuthType.USER, it, userName)
        }
    }

    /**
     * Adds a set of permissions for a group.
     *
     * @since 1.88
     */
    void groupPermissions(String groupName, Iterable<String> permissionsList) {
        permissionsList.each {
            this.addAuthTypedPermission(AuthType.GROUP, it, groupName)
        }
    }

    protected void addPermission(String permission, String user) {
        permissions << "${permission}:${user}".toString()
    }

    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '3.0')
    protected void addAuthTypedPermission(AuthType authType, String permission, String user) {
        permissions << "${authType.toString()}:${permission}:${user}".toString()
    }

    protected Set<String> getAvailablePermissions() {
        jobManagement.getPermissions(authorizationMatrixPropertyClassName)
    }
}
