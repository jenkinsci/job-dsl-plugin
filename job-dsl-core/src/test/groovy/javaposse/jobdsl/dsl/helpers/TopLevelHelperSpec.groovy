package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class TopLevelHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    JobManagement mockJobManagement = Mock()
    TopLevelHelper helper = new TopLevelHelper(mockActions, JobType.Freeform, mockJobManagement)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))

    def 'add description'() {
        when:
        def action = helper.description('Description')
        action.execute(root)

        then:
        root.description[0].value() == 'Description'

        when:
        def action2 = helper.description('Description2')
        action2.execute(root)

        then:
        root.description.size() == 1
        root.description[0].value() == 'Description2'

    }

    def 'environments work with map arg'() {
        when:
        def action = helper.environmentVariables([
                key1: 'val1',
                key2: 'val2'
        ])
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
    }

    def 'environments work with context'() {
        when:
        def action = helper.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
    }

    def 'environments work with combination'() {
        when:
        def action = helper.environmentVariables([key4: 'val4']) {
            env 'key3', 'val3'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key4=val4')
    }

    def 'environment from groovy script'() {
        when:
        def action = helper.environmentVariables {
            groovy '[foo: "bar"]'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    def 'environment from map and groovy script'() {
        when:
        def action = helper.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
            groovy '[foo: "bar"]'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    def 'can run label'() {
        when:
        helper.label('RPM')

        then:
        1 * mockActions.add(_)
    }

    def 'disable defaults to true'() {
        when:
        helper.disabled().execute(root)

        then:
        root.disabled.size() == 1
        root.disabled[0].value() == 'true'

        when:
        helper.disabled(false).execute(root)

        then:
        root.disabled.size() == 1
        root.disabled[0].value() == 'false'
    }

    def 'label constructs xml'() {
        when:
        def action = helper.label('FullTools')
        action.execute(root)

        then:
        root.assignedNode[0].value() == 'FullTools'
        root.canRoam[0].value() == 'false'
    }

    def 'without label leaves canRoam as true'() {
        when:
        when:
        def action = helper.label()
        action.execute(root)

        then:
        root.assignedNode[0].value() == ''
        root.canRoam[0].value() == 'true'
    }

    def 'log rotate xml'() {
        when:
        def action = helper.logRotator(14,50)
        action.execute(root)

        then:
        root.logRotator[0].daysToKeep[0].value() == '14'
        root.logRotator[0].numToKeep[0].value() == '50'
    }

    def 'build blocker xml'() {
        when:
        def action = helper.blockOn("MyProject")
        action.execute(root)

        then:
        root.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0].useBuildBlocker[0].value() == 'true'
        root.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0].blockingJobs[0].value() == 'MyProject'
    }

    def 'can run jdk'() {
        when:
        def action = helper.jdk("JDK1.6.0_32")
        action.execute(root)

        then:
        root.jdk[0].value() == "JDK1.6.0_32"
    }

    def 'can run jdk twice'() {
        when:
        helper.jdk("JDK1.6.0_16").execute(root)

        then:
        root.jdk[0].value() == "JDK1.6.0_16"

        when:
        helper.jdk("JDK1.6.0_17").execute(root)

        then:
        root.jdk.size() == 1
        root.jdk[0].value() == "JDK1.6.0_17"
    }

    def 'priority constructs xml'() {
        when:
        def action = helper.priority(99)
        action.execute(root)

        then:
        root.properties.'hudson.queueSorter.PrioritySorterJobProperty'.priority[0].value() == 99
    }

    def 'add a quiet period'() {
        when:
        def action = helper.quietPeriod()
        action.execute(root)

        then:
        root.quietPeriod[0].value() == 5

        when:
        action = helper.quietPeriod(10)
        action.execute(root)

        then:
        root.quietPeriod[0].value() == 10
    }

    def 'add SCM retry count' () {
        when:
        def action = helper.checkoutRetryCount()
        action.execute(root)

        then:
        root.scmCheckoutRetryCount[0].value() == 3

        when:
        action = helper.checkoutRetryCount(6)
        action.execute(root)

        then:
        root.scmCheckoutRetryCount[0].value() == 6
    }

    def 'add display name' () {
        when:
        def action = helper.displayName('FooBar')
        action.execute(root)

        then:
        root.displayName[0].value() == 'FooBar'
    }

    def 'add custom workspace' () {
        when:
        def action = helper.customWorkspace('/var/lib/jenkins/foobar')
        action.execute(root)

        then:
        root.customWorkspace[0].value() == '/var/lib/jenkins/foobar'
    }

    def 'add block for up and downstream projects' () {
        when:
        def action = helper.blockOnUpstreamProjects()
        action.execute(root)

        then:
        root.blockBuildWhenDownstreamBuilding[0].value() == true

        when:
        action = helper.blockOnDownstreamProjects()
        action.execute(root)

        then:
        root.blockBuildWhenUpstreamBuilding[0].value() == true
    }

    def 'set keep Dependencies'(keep) {
        when:
        def action = helper.keepDependencies(keep)
        action.execute(root)

        then:
        root.keepDependencies.size() == 1
        root.keepDependencies[0].value() == keep

        where:
        keep << [true, false]
    }

    def 'sshAgent without credentials' () {
        when:
        def action = helper.sshAgent(null)
        action.execute(root)

        then:
        thrown(NullPointerException)
    }

    def 'sshAgent with invalid credentials' () {
        setup:
        mockJobManagement.getCredentialsId('foo') >> null

        when:
        def action = helper.sshAgent('foo')
        action.execute(root)

        then:
        thrown(NullPointerException)
    }

    def 'sshAgent' () {
        setup:
        mockJobManagement.getCredentialsId('acme') >> '4711'

        when:
        def action = helper.sshAgent('acme')
        action.execute(root)

        then:
        def wrapper = root.buildWrappers[0].'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper'
        wrapper.user[0].value() == '4711'
    }
}
