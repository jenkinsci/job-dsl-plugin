package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class MultibranchWorkflowTriggerContext extends ItemTriggerContext {
    MultibranchWorkflowTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }
}
