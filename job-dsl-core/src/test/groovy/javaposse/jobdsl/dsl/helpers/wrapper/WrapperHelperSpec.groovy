package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout.absolute
import static javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout.elastic
import static javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout.likelyStuck

class WrapperHelperSpec extends Specification {
    List<WithXmlAction> mockActions = new ArrayList()
    JobManagement mockJobManagement = Mock()
    WrapperContextHelper helper = new WrapperContextHelper(mockActions, JobType.Freeform, mockJobManagement)
    WrapperContext context = new WrapperContext(JobType.Freeform, mockJobManagement)
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
        helper.wrappers {
            runOnSameNodeAs('testJob')
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == false
    }

    def 'run on same node and use same workspace' () {
        when:
        helper.wrappers {
            runOnSameNodeAs('testJob', true)
        }
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
        helper.wrappers {
            rvm('ruby-1.9.3')
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].impl[0].value() == 'ruby-1.9.3'
    }

    def 'rvm exception on empty param'() {
        when:
        helper.wrappers {
            rvm()
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'can run timeout'() {
        when:
        helper.wrappers {
            timeout(15)
        }

        then:
        mockActions.size() == 1
    }

    def 'timeout constructs xml'() {
        when:
        helper.wrappers {
            timeout(15)
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].timeoutMinutes[0].value() == '15'
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].failBuild[0].value() == 'true'
    }

    def 'timeout failBuild parameter works'() {
        when:
        helper.wrappers {
            timeout(15, false)
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'[0].failBuild[0].value() == 'false'
    }

    def 'default timeout works' () {
        when:
        helper.wrappers {
            timeout()
        }
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
        helper.wrappers {
            timeout('absolute') {
                limit 5
            }
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
        helper.wrappers {
            timeout('elastic') {
                limit 15
                percentage 200
            }
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
        helper.wrappers {
            timeout('likelyStuck')
        }
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
        helper.wrappers {
            allocatePorts 'HTTP', '8080'
        }
        executeHelperActionsOnRootNode()

        then:
        def ports = root.buildWrappers.'org.jvnet.hudson.plugins.port__allocator.PortAllocator'.ports
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[0].name[0].value() == 'HTTP'
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[1].name[0].value() == '8080'
    }

    def 'port allocator closure'() {
        when:
        helper.wrappers {
            allocatePorts {
                port 'HTTP'
                port '8080'
                glassfish '1234', 'user', 'password'
                tomcat '1234', 'password'
            }
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


    def 'sshAgent without credentials' () {
        when:
        helper.wrappers {
            sshAgent(null)
        }

        then:
        thrown(NullPointerException)
    }

    def 'sshAgent with invalid credentials' () {
        setup:
        mockJobManagement.getCredentialsId('foo') >> null

        when:
        helper.wrappers {
            sshAgent('foo')
        }

        then:
        thrown(NullPointerException)
    }

    def 'sshAgent' () {
        setup:
        mockJobManagement.getCredentialsId('acme') >> '4711'

        when:
        helper.wrappers {
            sshAgent('acme')
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper'
        wrapper.user[0].value() == '4711'
    }

    def 'ansiColor with map' () {
        when:
        helper.wrappers {
            colorizeOutput('foo')
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.ansicolor.AnsiColorBuildWrapper'.'colorMapName'
        wrapper[0].value() == 'foo'
    }

    def 'ansiColor without map should fall back to default xterm' () {
        when:
        helper.wrappers {
            colorizeOutput()
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.ansicolor.AnsiColorBuildWrapper'.'colorMapName'
        wrapper[0].value() == 'xterm'
    }

    def 'xvnc' () {
        when:
        helper.wrappers {
            xvnc()
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.xvnc.Xvnc'.takeScreenshot
        wrapper[0].value() == false
    }

    def 'xvnc with takeScreenshot arg' () {
        when:
        helper.wrappers {
            xvnc(true)
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.xvnc.Xvnc'.takeScreenshot
        wrapper[0].value() == true
    }

    def 'toolenv' () {
        when:
        helper.wrappers {
            toolenv("Ant 1.8.2", "Maven 3")
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.toolenv.ToolEnvBuildWrapper'.'vars'
        wrapper[0].value() == "ANT_1_8_2_HOME,MAVEN_3_HOME"
    }

    def 'environmentVariables are added'() {
        when:
        context.environmentVariables {
            propertiesFile 'some.properties'
            envs test: 'some', other: 'any'
            env 'some', 'value'
            script 'echo Test'
            scriptFile '/var/lib/jenkins'
        }
        Node envNode = context.wrapperNodes[0]

        then:
        envNode.name() == 'EnvInjectBuildWrapper'
        def infoNode = envNode.info[0]
        infoNode.children().size() == 5
        infoNode.propertiesFilePath[0].value() == 'some.properties'
        infoNode.propertiesContent[0].value() == 'test=some\nother=any\nsome=value'
        infoNode.scriptFilePath[0].value() == '/var/lib/jenkins'
        infoNode.scriptContent[0].value() == 'echo Test'
        infoNode.loadFilesFromMaster[0].value() == false
    }

    def 'configure m2release plugin with least args'() {
        when:
        context = new WrapperContext(JobType.Maven, mockJobManagement)
        context.mavenRelease()

        then:
        context.wrapperNodes.size() == 1
        def m2releaseNode = context.wrapperNodes[0]

        m2releaseNode.scmUserEnvVar[0].value() == ''
        m2releaseNode.scmPasswordEnvVar[0].value() == ''
        m2releaseNode.releaseEnvVar[0].value() == 'IS_M2RELEASEBUILD'
        m2releaseNode.releaseGoals[0].value() == '-Dresume=false release:prepare release:perform'
        m2releaseNode.dryRunGoals[0].value() == '-Dresume=false -DdryRun=true release:prepare'
        m2releaseNode.selectCustomScmCommentPrefix[0].value() == false
        m2releaseNode.selectAppendHudsonUsername[0].value() == false
        m2releaseNode.selectScmCredentials[0].value() == false
        m2releaseNode.numberOfReleaseBuildsToKeep[0].value() == 1
    }

    def 'configure m2release plugin with all args'() {
        when:
        context = new WrapperContext(JobType.Maven, mockJobManagement)
        context.mavenRelease() {
            scmUserEnvVar 'MY_USER_ENV'
            scmPasswordEnvVar 'MY_PASSWORD_ENV'
            releaseEnvVar 'RELEASE_ENV'
            releaseGoals 'release:prepare release:perform'
            dryRunGoals '-DdryRun=true release:prepare'
            selectCustomScmCommentPrefix()
            selectAppendHudsonUsername()
            selectScmCredentials()
            numberOfReleaseBuildsToKeep 10
        }

        then:
        context.wrapperNodes.size() == 1
        def m2releaseNode = context.wrapperNodes[0]

        m2releaseNode.scmUserEnvVar[0].value() == 'MY_USER_ENV'
        m2releaseNode.scmPasswordEnvVar[0].value() == 'MY_PASSWORD_ENV'
        m2releaseNode.releaseEnvVar[0].value() == 'RELEASE_ENV'
        m2releaseNode.releaseGoals[0].value() == 'release:prepare release:perform'
        m2releaseNode.dryRunGoals[0].value() == '-DdryRun=true release:prepare'
        m2releaseNode.selectCustomScmCommentPrefix[0].value() == true
        m2releaseNode.selectAppendHudsonUsername[0].value() == true
        m2releaseNode.selectScmCredentials[0].value() == true
        m2releaseNode.numberOfReleaseBuildsToKeep[0].value() == 10
    }

    def 'configure m2release plugin with FreeForm job should fail'() {
        when:
        context.mavenRelease()

        then:
        thrown IllegalStateException
    }
}
