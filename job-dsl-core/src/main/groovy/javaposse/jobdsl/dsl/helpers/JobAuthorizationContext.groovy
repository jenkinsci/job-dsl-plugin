package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement

class JobAuthorizationContext extends AuthorizationContext {
    JobAuthorizationContext(JobManagement jobManagement) {
        super(jobManagement, 'hudson.security.AuthorizationMatrixProperty')
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
