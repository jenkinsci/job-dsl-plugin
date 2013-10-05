package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.TopLevelHelper.Timeout.*

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

    def 'add rvm-controlled ruby version'() {
        when:
        def action = helper.rvm('ruby-1.9.3')
        action.execute(root)

        then:
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].impl[0].value() == 'ruby-1.9.3'
    }

    def 'rvm exception on empty param'() {
        when:
        def action = helper.rvm()

        then:
        thrown(IllegalArgumentException)
    }

    def 'can run timeout'() {
        when:
        helper.timeout(15)

        then:
        1 * mockActions.add(_)
    }

    def 'timeout constructs xml'() {
        when:
        def action = helper.timeout(15)
        action.execute(root)

        then:
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].timeoutMinutes[0].value() == '15'
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].failBuild[0].value() == 'true'
    }

    def 'timeout failBuild parameter works'() {
        when:
        def action = helper.timeout(15, false)
        action.execute(root)

        then:
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].failBuild[0].value() == 'false'
    }

    def 'default timeout works' () {
        when:
        def action = helper.timeout()
        action.execute(root)

        then:
        def timeout = root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'
        timeout.timeoutMinutes[0].value() == 3
        timeout.failBuild[0].value() == false
        timeout.writingDescription[0].value() == false
        timeout.timeoutPercentage[0].value() ==  0
        timeout.timeoutType[0].value() == absolute
        timeout.timeoutMinutesElasticDefault[0].value() == 3
    }

    def 'absolute timeout configuration working' () {
        when:
        def action = helper.timeout('absolute') {
            limit 5
        }
        action.execute(root)

        then:
        def timeout = root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'
        timeout.timeoutMinutes[0].value() == 5
        timeout.failBuild[0].value() == false
        timeout.writingDescription[0].value() == false
        timeout.timeoutPercentage[0].value() ==  0
        timeout.timeoutType[0].value() == absolute
        timeout.timeoutMinutesElasticDefault[0].value() == 5
    }


    def 'elastic timeout configuration working' () {
        when:
        def action = helper.timeout('elastic') {
            limit 15
            percentage 200
        }
        action.execute(root)

        then:
        def timeout = root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'
        timeout.timeoutMinutes[0].value() == 15
        timeout.failBuild[0].value() == false
        timeout.writingDescription[0].value() == false
        timeout.timeoutPercentage[0].value() ==  200
        timeout.timeoutType[0].value() == elastic
        timeout.timeoutMinutesElasticDefault[0].value() == 15
    }

    def 'likelyStuck timeout configuration working' () {
        when:
        def action = helper.timeout('likelyStuck')
        action.execute(root)

        then:
        def timeout = root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'
        timeout.timeoutMinutes[0].value() == 3
        timeout.failBuild[0].value() == false
        timeout.writingDescription[0].value() == false
        timeout.timeoutPercentage[0].value() ==  0
        timeout.timeoutType[0].value() == likelyStuck
        timeout.timeoutMinutesElasticDefault[0].value() == 3
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

    def 'port allocator string list'() {
        when:
        def action = helper.allocatePorts 'HTTP', '8080'
        action.execute(root)

        then:
        def ports = root.buildWrappers.'org.jvnet.hudson.plugins.port__allocator.PortAllocator'.ports
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[0].name[0].value() == 'HTTP'
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[1].name[0].value() == '8080'
    }

    def 'port allocator closure'() {
        when:
        def action = helper.allocatePorts {
            port 'HTTP'
            port '8080'
            glassfish '1234', 'user', 'password'
            tomcat '1234', 'password'
        }

        action.execute(root)

        then:
        def ports = root.buildWrappers[0].'org.jvnet.hudson.plugins.port__allocator.PortAllocator'[0].ports
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[0].name[0].value() == 'HTTP'
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[1].name[0].value() == '8080'

        /*def glassfish  = ports['org.jvnet.hudson.plugins.port__allocator.GlassfishJmxPortType']
        glassfish.name[0].value()== '1234'
        glassfish.userName[0].value()== 'username'
        glassfish.password[0].value()== 'password'

        def tomcat = ports.'org.jvnet.hudson.plugins.port__allocator.TomcatShutdownPortType'
        tomcat.name[0].value()== '1234'
        tomcat.password[0].value()== 'password' */
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

    def 'run on same node' () {
        when:
        def action = helper.runOnSameNodeAs('testJob')
        action.execute(root)

        then:
        def wrapper = root.buildWrappers[0].'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == false
    }

    def 'run on same node and use same workspace' () {
        when:
        def action = helper.runOnSameNodeAs('testJob', true)
        action.execute(root)

        then:
        def wrapper = root.buildWrappers[0].'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == true
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
