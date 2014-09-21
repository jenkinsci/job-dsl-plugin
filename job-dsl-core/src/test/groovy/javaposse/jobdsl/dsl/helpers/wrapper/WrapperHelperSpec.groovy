package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout

class WrapperHelperSpec extends Specification {
    List<WithXmlAction> mockActions = []
    JobManagement mockJobManagement = Mock()
    WrapperContextHelper helper = new WrapperContextHelper(mockActions, JobType.Freeform, mockJobManagement)
    WrapperContext context = new WrapperContext(JobType.Freeform, mockJobManagement)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

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

    def 'can not run timeout with empty closure'() {
        when:
        helper.wrappers {
            timeout {
            }
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'timeout constructs xml'() {
        when:
        helper.wrappers {
            timeout(15)
        }
        executeHelperActionsOnRootNode()

        then:
        def strategy = timeoutWrapper.strategy[0]
        strategy.attribute('class') == 'hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy'
        strategy.timeoutMinutes[0].value() == 15
        timeoutWrapper.operationList.size() == 1
        timeoutHasFailOperation()
    }

    private void timeoutHasFailOperation() {
        assert timeoutWrapper.operationList[0].'hudson.plugins.build__timeout.operations.FailOperation'[0] != null
    }

    def 'timeout failBuild parameter works'() {
        when:
        helper.wrappers {
            timeout(15, false)
        }
        executeHelperActionsOnRootNode()

        then:
        timeoutHasNoOperation()
    }

    private void timeoutHasNoOperation() {
        assert timeoutWrapper.operationList[0].children().size() == 0
    }

    def 'default timeout works' () {
        when:
        helper.wrappers {
            timeout()
        }
        executeHelperActionsOnRootNode()

        then:
        timeoutWrapper.strategy[0].timeoutMinutes[0].value() == 3
        timeoutHasNoOperation()
    }

    def 'absolute timeout configuration working' () {
        when:
        helper.wrappers {
            timeout {
                absolute(5)
            }
        }
        executeHelperActionsOnRootNode()

        then:
        timeoutWrapper.strategy[0].timeoutMinutes[0].value() == 5
        timeoutHasNoOperation()
    }

    private getTimeoutWrapper() {
        root.buildWrappers[0].'hudson.plugins.build__timeout.BuildTimeoutWrapper'
    }

    def 'elastic timeout configuration working' () {
        when:
        helper.wrappers {
            timeout {
                elastic(200, 3, 15)
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def strategy = timeoutWrapper.strategy[0]
        strategy.timeoutMinutesElasticDefault[0].value() == 15
        strategy.timeoutPercentage[0].value() == 200
        strategy.attribute('class') == Timeout.elastic.className
        timeoutHasNoOperation()
    }

    def 'NoActivity configuration working with set description' () {
        when:
        helper.wrappers {
            timeout {
                noActivity(15)
                writeDescription('desc')
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def strategy = timeoutWrapper.strategy[0]
        strategy.timeout[0].value() == 15000
        strategy.attribute('class') == Timeout.noActivity.className
        def list = timeoutWrapper.operationList[0]
        list.'hudson.plugins.build__timeout.operations.WriteDescriptionOperation'[0].description[0].value() == 'desc'
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.13')
    }

    def 'likelyStuck timeout configuration working' () {
        when:
        helper.wrappers {
            timeout {
                likelyStuck()
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def strategy = timeoutWrapper.strategy[0]
        strategy.attribute('class') == Timeout.likelyStuck.className
        strategy.children().size() == 0
        timeoutHasNoOperation()
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
            toolenv('Ant 1.8.2', 'Maven 3')
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.toolenv.ToolEnvBuildWrapper'.'vars'
        wrapper[0].value() == 'ANT_1_8_2_HOME,MAVEN_3_HOME'
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

    def 'release plugin simple' () {
        when:
        helper.wrappers {
            release {
                parameters {
                    textParam('p1', 'p1', 'd1')
                }
                preBuildSteps {
                    shell('echo hello;')
                }
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'hudson.plugins.release.ReleaseWrapper'
        wrapper.'parameterDefinitions'.'hudson.model.TextParameterDefinition'[0].value()[0].value() == 'p1'
        wrapper.'preBuildSteps'[0].value()[0].name() == 'hudson.tasks.Shell'
        wrapper.'preBuildSteps'[0].value()[0].value()[0].name() == 'command'
        wrapper.'preBuildSteps'[0].value()[0].value()[0].value() == 'echo hello;'
    }

    def 'release plugin extended' () {
        when:
        helper.wrappers {
            release {
                releaseVersionTemplate('templatename')
                doNotKeepLog(true)
                overrideBuildParameters(false)
                parameters {
                    booleanParam('myBooleanParam', true)
                    booleanParam('my2ndBooleanParam', true)
                }
                postSuccessfulBuildSteps {
                    shell('echo postsuccess;')
                    shell('echo hello world;')
                }
                postBuildSteps {
                    shell('echo post;')
                }
                postFailedBuildSteps {
                    shell('echo postfailed;')
                }
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def params = root.buildWrappers[0].'hudson.plugins.release.ReleaseWrapper'
        params[0].value()[0].name() == 'releaseVersionTemplate'
        params[0].value()[0].value() == 'templatename'
        params[0].value()[1].name() == 'doNotKeepLog'
        params[0].value()[1].value() == true
        params[0].value()[2].name() == 'overrideBuildParameters'
        params[0].value()[2].value() == false

        def stepsPostSuccess = root.buildWrappers[0].'hudson.plugins.release.ReleaseWrapper'.'postSuccessfulBuildSteps'
        stepsPostSuccess[0].value()[0].name() == 'hudson.tasks.Shell'
        stepsPostSuccess[0].value()[0].value()[0].name() == 'command'
        stepsPostSuccess[0].value()[0].value()[0].value() == 'echo postsuccess;'
        stepsPostSuccess[0].value()[1].name() == 'hudson.tasks.Shell'
        stepsPostSuccess[0].value()[1].value()[0].name() == 'command'
        stepsPostSuccess[0].value()[1].value()[0].value() == 'echo hello world;'

        def stepsPost = root.buildWrappers[0].'hudson.plugins.release.ReleaseWrapper'.'postBuildSteps'
        stepsPost[0].value()[0].name() == 'hudson.tasks.Shell'
        stepsPost[0].value()[0].value()[0].name() == 'command'
        stepsPost[0].value()[0].value()[0].value() == 'echo post;'

        def stepsPostFailed = root.buildWrappers[0].'hudson.plugins.release.ReleaseWrapper'.'postFailedBuildSteps'
        stepsPostFailed[0].value()[0].name() == 'hudson.tasks.Shell'
        stepsPostFailed[0].value()[0].value()[0].name() == 'command'
        stepsPostFailed[0].value()[0].value()[0].value() == 'echo postfailed;'
    }

    def 'release plugin configure' () {
        when:
        helper.wrappers {
            release {
                configure { project ->
                    def node = project / 'testCommand'
                    node << {
                        custom('value')
                    }
                }
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def params = root.buildWrappers[0].'hudson.plugins.release.ReleaseWrapper'.'testCommand'
        params[0].value()[0].name() == 'custom'
        params[0].value()[0].value() == 'value'
    }

    def 'call preBuildCleanup with minimal options' () {
        when:
        helper.wrappers {
            preBuildCleanup()
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'hudson.plugins.ws__cleanup.PreBuildCleanup'
            children().size() == 4
            patterns[0].value() == []
            deleteDirs[0].value() == false
            cleanupParameter[0].value() == ''
            externalDelete[0].value() == ''
        }
    }

    def 'call preBuildCleanup with all options' () {
        when:
        helper.wrappers {
            preBuildCleanup {
                includePattern('**/test/**')
                excludePattern('*.test')
                deleteDirectories()
                cleanupParameter('TEST')
                deleteCommand('test')
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'hudson.plugins.ws__cleanup.PreBuildCleanup'
            children().size() == 4
            patterns[0].children().size() == 2
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].children().size() == 2
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].pattern[0].value() == '**/test/**'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[0].type[0].value() == 'INCLUDE'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].children().size() == 2
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].pattern[0].value() == '*.test'
            patterns[0].'hudson.plugins.ws__cleanup.Pattern'[1].type[0].value() == 'EXCLUDE'
            deleteDirs[0].value() == true
            cleanupParameter[0].value() == 'TEST'
            externalDelete[0].value() == 'test'
        }
    }

    def 'logSizeChecker with default configuration'() {
        when:
        helper.wrappers {
            logSizeChecker()
        }

        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper'
            setOwn[0].value() == false
            maxLogSize[0].value() == 0
            failBuild[0].value() == false
        }
    }

    def 'logSizeChecker with configuration for all parameters'() {
        when:
        helper.wrappers {
            logSizeChecker {
                maxSize(10)
                failBuild(true)
            }
        }

        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper'
            setOwn[0].value() == true
            maxLogSize[0].value() == 10
            failBuild[0].value() == true
        }
    }

    def 'logSizeChecker with configuration for all parameters using defaults for boolean parameter'() {
        when:
        helper.wrappers {
            logSizeChecker {
                maxSize(10)
                failBuild()
            }
        }

        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper'
            setOwn[0].value() == true
            maxLogSize[0].value() == 10
            failBuild[0].value() == true
        }
    }

    def 'logSizeChecker with invalid maxSize'() {
        when:
        helper.wrappers {
            logSizeChecker {
                maxSize(-1)
            }
        }

        executeHelperActionsOnRootNode()

        then:
        thrown(IllegalArgumentException)
    }

    def 'call injectPasswords'() {
        when:
        helper.wrappers {
            injectPasswords()
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'EnvInjectPasswordWrapper'
            children().size() == 2
            children()[0].name() == 'injectGlobalPasswords'
            children()[0].value() == true
        }
    }

    def 'call buildName' () {
        when:
        helper.wrappers {
            buildName('#${BUILD_NUMBER} && <test>')
        }
        executeHelperActionsOnRootNode()

        then:
        def wrapper = root.buildWrappers[0].'org.jenkinsci.plugins.buildnamesetter.BuildNameSetter'
        wrapper.template[0].value() == '#${BUILD_NUMBER} && <test>'
    }

    def 'call buildName with null parameter' () {
        when:
        helper.wrappers {
            buildName(null)
        }

        then:
        thrown(NullPointerException)
    }

    def 'call codeSigning with no args'() {
        when:
        helper.wrappers {
            keychains {
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'com.sic.plugins.kpp.KPPKeychainsBuildWrapper'
            keychainCertificatePairs[0].children().size() == 0
            deleteKeychainsAfterBuild[0].value() == false
            overwriteExistingKeychains[0].value() == false
        }
    }

    def 'call codeSigning with minimal args'() {
        when:
        helper.wrappers {
            keychains {
                keychain('some_keychain', 'some_identity')
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'com.sic.plugins.kpp.KPPKeychainsBuildWrapper'
            def certPair = keychainCertificatePairs[0].'com.sic.plugins.kpp.model.KPPKeychainCertificatePair'[0]
            certPair.keychain[0].value() == 'some_keychain'
            certPair.codeSigningIdentity[0].value() == 'some_identity'
            certPair.varPrefix[0].value() == ''
            deleteKeychainsAfterBuild[0].value() == false
            overwriteExistingKeychains[0].value() == false
        }
    }

    def 'call codeSigning with all args'() {
        when:
        helper.wrappers {
            keychains {
                keychain('some_keychain', 'some_identity', 'some_prefix')
                keychain('some_keychain_again', 'some_identity_again', 'some_prefix_again')
                delete()
                overwrite()
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'com.sic.plugins.kpp.KPPKeychainsBuildWrapper'

            def certPair0 = keychainCertificatePairs[0].'com.sic.plugins.kpp.model.KPPKeychainCertificatePair'[0]
            certPair0.keychain[0].value() == 'some_keychain'
            certPair0.codeSigningIdentity[0].value() == 'some_identity'
            certPair0.varPrefix[0].value() == 'some_prefix'

            def certPair1 = keychainCertificatePairs[0].'com.sic.plugins.kpp.model.KPPKeychainCertificatePair'[1]
            certPair1.keychain[0].value() == 'some_keychain_again'
            certPair1.codeSigningIdentity[0].value() == 'some_identity_again'
            certPair1.varPrefix[0].value() == 'some_prefix_again'

            deleteKeychainsAfterBuild[0].value() == true
            overwriteExistingKeychains[0].value() == true
        }
    }

    def 'call exclusion with single arg'() {
        when:
        helper.wrappers {
            exclusionResources('first')
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'org.jvnet.hudson.plugins.exclusion.IdAllocator'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[0].name[0].value() == 'first'
        }
    }

    def 'call exclusion with multiple args'() {
        when:
        helper.wrappers {
            exclusionResources(['first', 'second', 'third'])
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].children().size() == 1
        with(root.buildWrappers[0].children()[0]) {
            name() == 'org.jvnet.hudson.plugins.exclusion.IdAllocator'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[0].name[0].value() == 'first'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[1].name[0].value() == 'second'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[2].name[0].value() == 'third'
        }
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
        context.mavenRelease {
            scmUserEnvVar 'MY_USER_ENV'
            scmPasswordEnvVar 'MY_PASSWORD_ENV'
            releaseEnvVar 'RELEASE_ENV'
            releaseGoals 'release:prepare release:perform'
            dryRunGoals '-DdryRun=true release:prepare'
            selectCustomScmCommentPrefix()
            selectAppendJenkinsUsername()
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

    def 'set delivery pipeline version'() {
        when:
        context.deliveryPipelineVersion('1.0.${BUILD_NUMBER}')

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'se.diabol.jenkins.pipeline.PipelineVersionContributor'
            children().size() == 2
            versionTemplate[0].value() == '1.0.${BUILD_NUMBER}'
            updateDisplayName[0].value() == false
        }
    }

    def 'set delivery pipeline version and display name'() {
        when:
        context.deliveryPipelineVersion('1.0.${BUILD_NUMBER}', true)

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'se.diabol.jenkins.pipeline.PipelineVersionContributor'
            children().size() == 2
            versionTemplate[0].value() == '1.0.${BUILD_NUMBER}'
            updateDisplayName[0].value() == true
        }
    }
}
