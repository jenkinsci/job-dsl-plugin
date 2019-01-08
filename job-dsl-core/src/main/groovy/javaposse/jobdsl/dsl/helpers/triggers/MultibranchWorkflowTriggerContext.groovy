package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class MultibranchWorkflowTriggerContext extends ItemTriggerContext {
    MultibranchWorkflowTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Triggers a build periodically if not otherwise run.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'cloudbees-folder', minimumVersion = '5.1')
    @Deprecated
    void periodic(int minutes) {
        triggerNodes << new NodeBuilder().'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger' {
            spec('* * * * *')
            interval(minutes * 60000)
        }
    }
}
