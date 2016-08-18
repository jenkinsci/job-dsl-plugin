package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class MavenTriggerContext extends TriggerContext {
    MavenTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * If set to <code>true</code>, Jenkins will parse the POMs of this project, and see if any of its snapshot
     * dependencies are built on this Jenkins as well. If so, Jenkins will set up build dependency relationship so that
     * whenever the dependency job is built and a new SNAPSHOT jar is created, Jenkins will schedule a build of this
     * project. Defaults to <code>false</code>.
     *
     * @param checkSnapshotDependencies set to <code>true</code> to check snapshot dependencies
     */
    void snapshotDependencies(boolean checkSnapshotDependencies) {
        item.configure {
            it.children().removeAll { it instanceof Node && it.name() == 'ignoreUpstremChanges' }
            it.appendNode 'ignoreUpstremChanges', !checkSnapshotDependencies
        }
    }
}
