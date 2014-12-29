package javaposse.jobdsl.plugin

import hudson.model.AbstractProject
import jenkins.model.Jenkins

import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author ceilfors
 */
class GeneratedJobMapHelper {

    private static final Logger LOGGER = Logger.getLogger(GeneratedJobMapHelper.name)

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
        try {
            project.updateTransientActions()
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, 'Transient action update attempt failed for item %s', project.fullName)
        }
    }
}
