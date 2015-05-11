package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement

class JobAuthorizationContext extends AuthorizationContext {
    boolean blocksInheritance = false

    JobAuthorizationContext(JobManagement jobManagement) {
        super(jobManagement, 'hudson.security.AuthorizationMatrixProperty')
    }

    void blocksInheritance(boolean blocksInheritance) {
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
