package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MultiBranchProjectFactoryContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    MultiBranchProjectFactoryContext context = new MultiBranchProjectFactoryContext(jobManagement, Mock(Item))

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.projectFactoryNodes[0] == node
    }

    def 'multibranch project with workflow branch project factory'() {
        when:
        context.workflowBranchProjectFactory {}

        then:
        context.projectFactoryNodes.size() == 1
        with(context.projectFactoryNodes[0]) {
            name() == 'org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory'
            with(getByName('owner')[0]) {
                attribute('class') == 'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject'
                attribute('reference') == '../..'
            }
            with(scriptPath[0]) {
                text() == 'Jenkinsfile'
            }
        }
        0 * jobManagement.requireMinimumPluginVersion('workflow-multibranch', '2.15')
    }

    def 'multibranch project with workflow branch project factory and custom script path'() {
        when:
        context.workflowBranchProjectFactory {
            scriptPath('scripts/pipeline.groovy')
        }

        then:
        context.projectFactoryNodes.size() == 1
        with(context.projectFactoryNodes[0]) {
            name() == 'org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory'
            with(getByName('owner')[0]) {
                attribute('class') == 'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject'
                attribute('reference') == '../..'
            }
            with(scriptPath[0]) {
                text() == 'scripts/pipeline.groovy'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('workflow-multibranch', '2.15')
    }
}
