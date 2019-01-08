package javaposse.jobdsl.plugin

import hudson.model.Descriptor
import hudson.security.PermissionGroup
import jenkins.model.Jenkins

class PermissionsHelper {
    static Set<String> getPermissions(String descriptorId) {
        Set<String> result = []
        Descriptor descriptor = Jenkins.get().getDescriptor(descriptorId)
        if (descriptor != null) {
            List<PermissionGroup> allGroups = descriptor.allGroups
            allGroups*.permissions.flatten().findAll { descriptor.showPermission(it) }.each {
                result << it.id
            }
        }
        result
    }
}
