package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.WrapperContext.Timeout.absolute
import static javaposse.jobdsl.dsl.helpers.WrapperContext.Timeout.elastic
import static javaposse.jobdsl.dsl.helpers.WrapperContext.Timeout.likelyStuck

class WrapperHelperSpec extends Specification {
    List<WithXmlAction> mockActions = new ArrayList()
    WrapperContextHelper helper = new WrapperContextHelper(mockActions, JobType.Freeform)
    WrapperContext context = new WrapperContext(JobType.Freeform)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))

    def 'call timestamps method'() {
        when:
        context.timestamps()

        then:
        context.wrapperNodes?.size() == 1

        def timestampWrapper = context.wrapperNodes[0]
        timestampWrapper.name() == 'hudson.plugins.timestamper.TimestamperBuildWrapper'
    }

    def 'run on same node' () {
        when:
        helper.runOnSameNodeAs('testJob')
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == false
    }

    def 'run on same node and use same workspace' () {
        when:
        helper.runOnSameNodeAs('testJob', true)
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == true
    }

    private executeHelperActionsOnRootNode() {
        helper.withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    def 'add rvm-controlled ruby version'() {
        when:
        helper.rvm('ruby-1.9.3')
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].impl[0].value() == 'ruby-1.9.3'
    }

    def 'rvm exception on empty param'() {
        when:
        helper.rvm()

        then:
        thrown(IllegalArgumentException)
    }

    def 'can run timeout'() {
        when:
        helper.timeout(15)

        then:
        mockActions.size() == 1
    }

    def 'timeout constructs xml'() {
        when:
        helper.timeout(15)
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].timeoutMinutes[0].value() == '15'
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].failBuild[0].value() == 'true'
    }

    def 'timeout failBuild parameter works'() {
        when:
        helper.timeout(15, false)
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].failBuild[0].value() == 'false'
    }

    def 'default timeout works' () {
        when:
        helper.timeout()
        executeHelperActionsOnRootNode()

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
        helper.timeout('absolute') {
            limit 5
        }
        executeHelperActionsOnRootNode()

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
        helper.timeout('elastic') {
            limit 15
            percentage 200
        }
        executeHelperActionsOnRootNode()

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
        helper.timeout('likelyStuck')
        executeHelperActionsOnRootNode()

        then:
        def timeout = root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'
        timeout.timeoutMinutes[0].value() == 3
        timeout.failBuild[0].value() == false
        timeout.writingDescription[0].value() == false
        timeout.timeoutPercentage[0].value() ==  0
        timeout.timeoutType[0].value() == likelyStuck
        timeout.timeoutMinutesElasticDefault[0].value() == 3
    }

    def 'port allocator string list'() {
        when:
        helper.allocatePorts 'HTTP', '8080'
        executeHelperActionsOnRootNode()

        then:
        def ports = root.buildWrappers.'org.jvnet.hudson.plugins.port__allocator.PortAllocator'.ports
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[0].name[0].value() == 'HTTP'
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[1].name[0].value() == '8080'
    }

    def 'port allocator closure'() {
        when:
        helper.allocatePorts {
            port 'HTTP'
            port '8080'
            glassfish '1234', 'user', 'password'
            tomcat '1234', 'password'
        }
        executeHelperActionsOnRootNode()

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

}
