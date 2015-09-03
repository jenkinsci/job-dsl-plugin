package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class JobAuthorizationContext extends AuthorizationContext {
    boolean blocksInheritance

    JobAuthorizationContext(JobManagement jobManagement) {
        super(jobManagement, 'hudson.security.AuthorizationMatrixProperty')
    }

    /**
     * Blocks inheritance of the global authorization matrix.
     *
     * @since 1.35
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '1.2')
    void blocksInheritance(boolean blocksInheritance = true) {
        this.blocksInheritance = blocksInheritance
    }

    @Deprecated
    void permission(Permissions permission, String user) {
        jobManagement.logDeprecationWarning()
        addPermission(permission.longForm, user)
    }

    @Override
    void permission(String permissionEnumName, String user) {
        try {
            super.permission(Permissions.valueOf(permissionEnumName).longForm, user)
            jobManagement.logDeprecationWarning('using the permission enum values')
        } catch (IllegalArgumentException ignore) {
            super.permission(permissionEnumName, user)
        }
    }
}
