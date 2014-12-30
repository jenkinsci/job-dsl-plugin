package javaposse.jobdsl.plugin

import hudson.model.AbstractProject
import jenkins.model.Jenkins

/**
 * @author ceilfors
 */
class GeneratedJobMapHelper {

    static boolean removeSeedReference(String key) {
        DescriptorImpl descriptor = Jenkins.instance.getDescriptorByType(DescriptorImpl)
        SeedReference seedReference = descriptor.generatedJobMap.remove(key)
        if (seedReference != null) {
            descriptor.save()
            return true
        } else {
            return false
        }
    }

    static void updateTransientActions(AbstractProject project) {
        project.updateTransientActions()
    }
}
