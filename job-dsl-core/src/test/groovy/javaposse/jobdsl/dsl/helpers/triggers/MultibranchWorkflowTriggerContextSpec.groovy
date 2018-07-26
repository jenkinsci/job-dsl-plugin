package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MultibranchWorkflowTriggerContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final MultibranchWorkflowTriggerContext context =
            new MultibranchWorkflowTriggerContext(jobManagement, Mock(Item))

    def 'call periodic folder trigger'() {
        when:
        context.periodic(1)

        then:
        context.triggerNodes.size() == 1
        with(context.triggerNodes[0]) {
            name() == 'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'
            children().size() == 2
            spec[0].value() == '* * * * *'
            interval[0].value() == 60000
        }
        1 * jobManagement.requireMinimumPluginVersion('cloudbees-folder', '5.1')
    }
}
