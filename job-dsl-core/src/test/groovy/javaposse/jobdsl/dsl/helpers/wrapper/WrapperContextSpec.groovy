package javaposse.jobdsl.dsl.helpers.wrapper

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import spock.lang.Specification

import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout

class WrapperContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)
    WrapperContext context = new WrapperContext(JobType.Freeform, mockJobManagement)

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
        context.runOnSameNodeAs('testJob')

        then:
        def wrapper = context.wrapperNodes[0]
        wrapper.name() == 'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == false
    }

    def 'run on same node and use same workspace' () {
        when:
        context.runOnSameNodeAs('testJob', true)

        then:
        def wrapper = context.wrapperNodes[0]
        wrapper.name() == 'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == true
    }

    private executeHelperActionsOnRootNode() {
        helper.withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    def 'add rbenv-controlled ruby version'() {
        when:
        helper.wrappers {
            rbenv('2.1.2')
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].version[0].value() == '2.1.2'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].gem__list[0].value() == ''
    }

    def 'add rbenv-controlled override defaults'() {
        when:
        helper.wrappers {
            rbenv('2.1.2',{ 
                root('foo') 
                rubyBuildRepository('foobar') 
                rubyBuildRevision('1.0') 
                rbenvRevision('2.0') 
                rbenvRepository('barfoo') 
                ignoreLocalVersion(true) })
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].version[0].value() == '2.1.2'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].gem__list[0].value() == ''
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].rbenv_root[0].value() == 'foo'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].ruby__build__repository[0].value() == 'foobar'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].rbenv__revision[0].value() == '2.0'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].rbenv__repository[0].value() == 'barfoo'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].ruby__build__revision[0].value() == '1.0'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].ignore__local__version[0].'@ruby-class' == 'TrueClass'

    }

    def 'add rbenv-controlled ruby version and gems'() {
        when:
        helper.wrappers {
            rbenv('2.1.2',['bundler','rake'])
        }
        executeHelperActionsOnRootNode()

        then:
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].version[0].value() == '2.1.2'
        root.buildWrappers[0].'ruby-proxy-object'[0].'ruby-object'[0].object[0].gem__list[0].value() == 'bundler,rake,'
    }

    def 'add rvm-controlled ruby version'() {
        when:
        context.rvm('ruby-1.9.3')

        then:
        context.wrapperNodes[0].name() == 'ruby-proxy-object'
        context.wrapperNodes[0].'ruby-object'[0].object[0].impl[0].value() == 'ruby-1.9.3'
    }

    def 'can not run timeout with empty closure'() {
        when:
        context.timeout {
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'timeout constructs xml'() {
        when:
        context.timeout(15)

        then:
        timeoutWrapper.name() == 'hudson.plugins.build__timeout.BuildTimeoutWrapper'
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
        context.timeout(15, false)

        then:
        timeoutHasNoOperation()
    }

    private void timeoutHasNoOperation() {
        assert timeoutWrapper.operationList[0].children().size() == 0
    }

    def 'default timeout works' () {
        when:
        context.timeout()

        then:
        timeoutWrapper.strategy[0].timeoutMinutes[0].value() == 3
        timeoutHasNoOperation()
    }

    def 'absolute timeout configuration working' () {
        when:
        context.timeout {
            absolute(5)
        }

        then:
        timeoutWrapper.strategy[0].timeoutMinutes[0].value() == 5
        timeoutHasNoOperation()
    }

    private getTimeoutWrapper() {
        context.wrapperNodes[0]
    }

    def 'elastic timeout configuration working' () {
        when:
        context.timeout {
            elastic(200, 3, 15)
        }

        then:
        def strategy = timeoutWrapper.strategy[0]
        strategy.timeoutMinutesElasticDefault[0].value() == 15
        strategy.timeoutPercentage[0].value() == 200
        strategy.attribute('class') == Timeout.elastic.className
        timeoutHasNoOperation()
    }

    def 'NoActivity configuration working with set description' () {
        when:
        context.timeout {
            noActivity(15)
            writeDescription('desc')
        }

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
        context.timeout {
            likelyStuck()
        }

        then:
        def strategy = timeoutWrapper.strategy[0]
        strategy.attribute('class') == Timeout.likelyStuck.className
        strategy.children().size() == 0
        timeoutHasNoOperation()
    }

    def 'port allocator string list'() {
        when:
        context.allocatePorts 'HTTP', '8080'

        then:
        context.wrapperNodes[0].name() == 'org.jvnet.hudson.plugins.port__allocator.PortAllocator'
        def ports = context.wrapperNodes[0].ports
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[0].name[0].value() == 'HTTP'
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[1].name[0].value() == '8080'
    }

    def 'port allocator closure'() {
        when:
        context.allocatePorts {
            port 'HTTP'
            port '8080'
            glassfish '1234', 'user', 'password'
            tomcat '1234', 'password'
        }

        then:
        context.wrapperNodes[0].name() == 'org.jvnet.hudson.plugins.port__allocator.PortAllocator'
        def ports = context.wrapperNodes[0].ports
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
        context.sshAgent(null)

        then:
        thrown(NullPointerException)
    }

    def 'sshAgent with invalid credentials' () {
        setup:
        mockJobManagement.getCredentialsId('foo') >> null

        when:
        context.sshAgent('foo')

        then:
        thrown(NullPointerException)
    }

    def 'sshAgent' () {
        setup:
        mockJobManagement.getCredentialsId('acme') >> '4711'

        when:
        context.sshAgent('acme')

        then:
        context.wrapperNodes[0].name() == 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper'
        context.wrapperNodes[0].user[0].value() == '4711'
    }

    def 'ansiColor with map' () {
        when:
        context.colorizeOutput('foo')

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.ansicolor.AnsiColorBuildWrapper'
        context.wrapperNodes[0].'colorMapName'[0].value() == 'foo'
    }

    def 'ansiColor without map should fall back to default xterm' () {
        when:
        context.colorizeOutput()

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.ansicolor.AnsiColorBuildWrapper'
        context.wrapperNodes[0].'colorMapName'[0].value() == 'xterm'
    }

    def 'xvnc' () {
        setup:
        mockJobManagement.getPluginVersion('xvnc') >> new VersionNumber('1.16')

        when:
        context.xvnc()

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.xvnc.Xvnc'
        def wrapper = context.wrapperNodes[0]
        wrapper.children().size() == 2
        wrapper.takeScreenshot[0].value() == false
        wrapper.useXauthority[0].value() == true
    }

    def 'xvnc with takeScreenshot arg' () {
        setup:
        mockJobManagement.getPluginVersion('xvnc') >> new VersionNumber('1.16')

        when:
        context.xvnc(true)

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.xvnc.Xvnc'
        def wrapper = context.wrapperNodes[0]
        wrapper.children().size() == 2
        wrapper.takeScreenshot[0].value() == true
        wrapper.useXauthority[0].value() == true
    }

    def 'xvnc with closure' () {
        setup:
        mockJobManagement.getPluginVersion('xvnc') >> new VersionNumber('1.16')

        when:
        context.xvnc {
            useXauthority(false)
        }

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.xvnc.Xvnc'
        def wrapper = context.wrapperNodes[0]
        wrapper.children().size() == 2
        wrapper.takeScreenshot[0].value() == false
        wrapper.useXauthority[0].value() == false
    }

    def 'xvnc with older plugin' () {
        setup:
        mockJobManagement.getPluginVersion('xvnc') >> new VersionNumber('1.15')

        when:
        context.xvnc()

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.xvnc.Xvnc'
        def wrapper = context.wrapperNodes[0]
        wrapper.children().size() == 1
        wrapper.takeScreenshot[0].value() == false
    }

    def 'toolenv' () {
        when:
        context.toolenv('Ant 1.8.2', 'Maven 3')

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.toolenv.ToolEnvBuildWrapper'
        context.wrapperNodes[0].'vars'[0].value() == 'ANT_1_8_2_HOME,MAVEN_3_HOME'
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
        context.release {
            parameters {
                textParam('p1', 'p1', 'd1')
            }
            preBuildSteps {
                shell('echo hello;')
            }
        }

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.release.ReleaseWrapper'
        def wrapper = context.wrapperNodes[0]
        wrapper.'parameterDefinitions'.'hudson.model.TextParameterDefinition'[0].value()[0].value() == 'p1'
        wrapper.'preBuildSteps'[0].value()[0].name() == 'hudson.tasks.Shell'
        wrapper.'preBuildSteps'[0].value()[0].value()[0].name() == 'command'
        wrapper.'preBuildSteps'[0].value()[0].value()[0].value() == 'echo hello;'
    }

    def 'release plugin extended' () {
        when:
        context.release {
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

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.release.ReleaseWrapper'
        def params = context.wrapperNodes[0]
        params.value()[0].name() == 'releaseVersionTemplate'
        params.value()[0].value() == 'templatename'
        params.value()[1].name() == 'doNotKeepLog'
        params.value()[1].value() == true
        params.value()[2].name() == 'overrideBuildParameters'
        params.value()[2].value() == false

        def stepsPostSuccess = context.wrapperNodes[0].'postSuccessfulBuildSteps'
        stepsPostSuccess[0].value()[0].name() == 'hudson.tasks.Shell'
        stepsPostSuccess[0].value()[0].value()[0].name() == 'command'
        stepsPostSuccess[0].value()[0].value()[0].value() == 'echo postsuccess;'
        stepsPostSuccess[0].value()[1].name() == 'hudson.tasks.Shell'
        stepsPostSuccess[0].value()[1].value()[0].name() == 'command'
        stepsPostSuccess[0].value()[1].value()[0].value() == 'echo hello world;'

        def stepsPost = context.wrapperNodes[0].'postBuildSteps'
        stepsPost[0].value()[0].name() == 'hudson.tasks.Shell'
        stepsPost[0].value()[0].value()[0].name() == 'command'
        stepsPost[0].value()[0].value()[0].value() == 'echo post;'

        def stepsPostFailed = context.wrapperNodes[0].'postFailedBuildSteps'
        stepsPostFailed[0].value()[0].name() == 'hudson.tasks.Shell'
        stepsPostFailed[0].value()[0].value()[0].name() == 'command'
        stepsPostFailed[0].value()[0].value()[0].value() == 'echo postfailed;'
    }

    def 'release plugin configure' () {
        when:
        context.release {
            configure { project ->
                def node = project / 'testCommand'
                node << {
                    custom('value')
                }
            }
        }

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.release.ReleaseWrapper'
        def params = context.wrapperNodes[0].'testCommand'
        params[0].value()[0].name() == 'custom'
        params[0].value()[0].value() == 'value'
    }

    def 'call preBuildCleanup with minimal options' () {
        when:
        context.preBuildCleanup()

        then:
        with(context.wrapperNodes[0]) {
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
        context.preBuildCleanup {
            includePattern('**/test/**')
            excludePattern('*.test')
            deleteDirectories()
            cleanupParameter('TEST')
            deleteCommand('test')
        }

        then:
        with(context.wrapperNodes[0]) {
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
        context.logSizeChecker()

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper'
            setOwn[0].value() == false
            maxLogSize[0].value() == 0
            failBuild[0].value() == false
        }
    }

    def 'logSizeChecker with configuration for all parameters'() {
        when:
        context.logSizeChecker {
            maxSize(10)
            failBuild(true)
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper'
            setOwn[0].value() == true
            maxLogSize[0].value() == 10
            failBuild[0].value() == true
        }
    }

    def 'logSizeChecker with configuration for all parameters using defaults for boolean parameter'() {
        when:
        context.logSizeChecker {
            maxSize(10)
            failBuild()
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper'
            setOwn[0].value() == true
            maxLogSize[0].value() == 10
            failBuild[0].value() == true
        }
    }

    def 'logSizeChecker with invalid maxSize'() {
        when:
        context.logSizeChecker {
            maxSize(-1)
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'call injectPasswords'() {
        when:
        context.injectPasswords()

        then:
        with(context.wrapperNodes[0]) {
            name() == 'EnvInjectPasswordWrapper'
            children().size() == 2
            children()[0].name() == 'injectGlobalPasswords'
            children()[0].value() == true
        }
    }

    def 'call buildName' () {
        when:
        context.buildName('#${BUILD_NUMBER} && <test>')

        then:
        context.wrapperNodes[0].name() == 'org.jenkinsci.plugins.buildnamesetter.BuildNameSetter'
        context.wrapperNodes[0].template[0].value() == '#${BUILD_NUMBER} && <test>'
    }

    def 'call buildName with null parameter' () {
        when:
        context.buildName(null)

        then:
        thrown(NullPointerException)
    }

    def 'call codeSigning with no args'() {
        when:
        context.keychains {
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.sic.plugins.kpp.KPPKeychainsBuildWrapper'
            keychainCertificatePairs[0].children().size() == 0
            deleteKeychainsAfterBuild[0].value() == false
            overwriteExistingKeychains[0].value() == false
        }
    }

    def 'call codeSigning with minimal args'() {
        when:
        context.keychains {
            keychain('some_keychain', 'some_identity')
        }

        then:
        with(context.wrapperNodes[0]) {
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
        context.keychains {
            keychain('some_keychain', 'some_identity', 'some_prefix')
            keychain('some_keychain_again', 'some_identity_again', 'some_prefix_again')
            delete()
            overwrite()
        }

        then:
        with(context.wrapperNodes[0]) {
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
        context.exclusionResources('first')

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.exclusion.IdAllocator'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[0].name[0].value() == 'first'
        }
    }

    def 'call exclusion with multiple args'() {
        when:
        context.exclusionResources(['first', 'second', 'third'])

        then:
        with(context.wrapperNodes[0]) {
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

    def 'call mask passwords'() {
        when:
        context.maskPasswords()

        then:
        context.wrapperNodes.size() == 1
        context.wrapperNodes[0].name() == 'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'
    }

    def 'call build user vars'() {
        when:
        context.buildUserVars()

        then:
        context.wrapperNodes.size() == 1
        context.wrapperNodes[0].name() == 'org.jenkinsci.plugins.builduser.BuildUser'
    }

    def 'call nodejs'() {
        when:
        context.nodejs('NodeJS 0.10.26')

        then:
        context.wrapperNodes?.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper'
            children().size() == 1
            nodeJSInstallationName[0].value() == 'NodeJS 0.10.26'
        }
    }

    def 'call golang'() {
        when:
        context.golang('Go 1.3.3')

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.golang.GolangBuildWrapper'
            goVersion[0].value() == 'Go 1.3.3'
        }
    }
}
