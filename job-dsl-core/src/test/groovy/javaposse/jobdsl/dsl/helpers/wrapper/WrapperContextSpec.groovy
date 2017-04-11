package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class WrapperContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    WrapperContext context = new WrapperContext(mockJobManagement, item)

    def 'call timestamps method'() {
        when:
        context.timestamps()

        then:
        context.wrapperNodes?.size() == 1
        def timestampWrapper = context.wrapperNodes[0]
        timestampWrapper.name() == 'hudson.plugins.timestamper.TimestamperBuildWrapper'
        1 * mockJobManagement.requirePlugin('timestamper')
    }

    def 'run on same node'() {
        when:
        context.runOnSameNodeAs('testJob')

        then:
        def wrapper = context.wrapperNodes[0]
        wrapper.name() == 'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == false
        1 * mockJobManagement.requirePlugin('job-node-stalker')
    }

    def 'run on same node and use same workspace'() {
        when:
        context.runOnSameNodeAs('testJob', true)

        then:
        def wrapper = context.wrapperNodes[0]
        wrapper.name() == 'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper'
        wrapper.job[0].value() == 'testJob'
        wrapper.shareWorkspace[0].value() == true
        1 * mockJobManagement.requirePlugin('job-node-stalker')
    }

    def 'add rbenv-controlled ruby version'() {
        when:
        context.rbenv('2.1.2')

        then:
        context.wrapperNodes[0].name() == 'ruby-proxy-object'
        def rootObject = context.wrapperNodes[0].'ruby-object'[0]
        rootObject.'@pluginid' == 'rbenv'
        rootObject.'@ruby-class' == 'Jenkins::Tasks::BuildWrapperProxy'
        rootObject.'pluginid'[0].value() == 'rbenv'
        rootObject.'pluginid'[0].'@ruby-class' == 'String'
        rootObject.'pluginid'[0].'@pluginid' == 'rbenv'
        rootObject.object[0].'@ruby-class' == 'RbenvWrapper'
        rootObject.object[0].'@pluginid' == 'rbenv'
        with(rootObject.object[0]) {
            version[0].value() == '2.1.2'
            version[0].'@pluginid' == 'rbenv'
            version[0].'@ruby-class' == 'String'
            ignore__local__version[0].value() == false
            ignore__local__version[0].'@pluginid' == 'rbenv'
            ignore__local__version[0].'@ruby-class' == 'String'
            gem__list[0].value() == ''
            gem__list[0].'@pluginid' == 'rbenv'
            gem__list[0].'@ruby-class' == 'String'
            rbenv__root[0].value() == '$HOME/.rbenv'
            rbenv__root[0].'@pluginid' == 'rbenv'
            rbenv__root[0].'@ruby-class' == 'String'
            rbenv__repository[0].value() == 'https://github.com/sstephenson/rbenv.git'
            rbenv__repository[0].'@pluginid' == 'rbenv'
            rbenv__repository[0].'@ruby-class' == 'String'
            rbenv__revision[0].value() == 'master'
            rbenv__revision[0].'@pluginid' == 'rbenv'
            rbenv__revision[0].'@ruby-class' == 'String'
            ruby__build__repository[0].value() == 'https://github.com/sstephenson/ruby-build.git'
            ruby__build__repository[0].'@pluginid' == 'rbenv'
            ruby__build__repository[0].'@ruby-class' == 'String'
            ruby__build__revision[0].value() == 'master'
            ruby__build__revision[0].'@pluginid' == 'rbenv'
            ruby__build__revision[0].'@ruby-class' == 'String'
        }
        1 * mockJobManagement.requirePlugin('rbenv')
        1 * mockJobManagement.requireMinimumPluginVersion('ruby-runtime', '0.12')
        1 * mockJobManagement.logPluginDeprecationWarning('rbenv', '0.0.17')
    }

    def 'add rbenv-controlled override defaults'() {
        when:
        context.rbenv('2.1.2') {
            ignoreLocalVersion(true)
            gems('bundler', 'rake')
            root('foo')
            rbenvRepository('barfoo')
            rbenvRevision('2.0')
            rubyBuildRepository('foobar')
            rubyBuildRevision('1.0')
        }

        then:
        context.wrapperNodes[0].name() == 'ruby-proxy-object'
        with(context.wrapperNodes[0].'ruby-object'[0].object[0]) {
            version[0].value() == '2.1.2'
            ignore__local__version[0].value() == true
            gem__list[0].value() == 'bundler,rake'
            rbenv__root[0].value() == 'foo'
            rbenv__repository[0].value() == 'barfoo'
            rbenv__revision[0].value() == '2.0'
            ruby__build__repository[0].value() == 'foobar'
            ruby__build__revision[0].value() == '1.0'
        }
        1 * mockJobManagement.requirePlugin('rbenv')
        1 * mockJobManagement.requireMinimumPluginVersion('ruby-runtime', '0.12')
        1 * mockJobManagement.logPluginDeprecationWarning('rbenv', '0.0.17')
    }

    def 'add rvm-controlled ruby version'() {
        when:
        context.rvm('ruby-1.9.3')

        then:
        context.wrapperNodes[0].name() == 'ruby-proxy-object'
        context.wrapperNodes[0].'ruby-object'[0].object[0].impl[0].value() == 'ruby-1.9.3'
        context.wrapperNodes[0].'ruby-object'[0].attribute('ruby-class') == 'Jenkins::Tasks::BuildWrapperProxy'
        1 * mockJobManagement.requireMinimumPluginVersion('rvm', '0.6')
        1 * mockJobManagement.requireMinimumPluginVersion('ruby-runtime', '0.12')
    }

    def 'default timeout works'() {
        when:
        context.timeout()

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].@class == 'hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy'
            strategy[0].timeoutMinutes[0].value() == 3
            operationList[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'absolute timeout configuration working'() {
        when:
        context.timeout {
            absolute(5)
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].@class == 'hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy'
            strategy[0].timeoutMinutes[0].value() == 5
            operationList[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'absolute timeout configuration working using string type'() {
        when:
        context.timeout {
            absolute('${TEST_JOB_TIMEOUT}')
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].@class == 'hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy'
            strategy[0].timeoutMinutes[0].value() == '${TEST_JOB_TIMEOUT}'
            operationList[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'elastic timeout configuration working'() {
        when:
        context.timeout {
            elastic(200, 3, 15)
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 3
            strategy[0].@class == 'hudson.plugins.build_timeout.impl.ElasticTimeOutStrategy'
            strategy[0].timeoutPercentage[0].value() == 200
            strategy[0].numberOfBuilds[0].value() == 3
            strategy[0].timeoutMinutesElasticDefault[0].value() == 15
            operationList[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'no activity timeout configuration working'() {
        when:
        context.timeout {
            noActivity(15)
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].@class == 'hudson.plugins.build_timeout.impl.NoActivityTimeOutStrategy'
            strategy[0].timeout[0].value() == 15000
            operationList[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.13')
    }

    def 'default timeout will set description'() {
        when:
        context.timeout {
            writeDescription('desc')
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].timeoutMinutes[0].value() == 3
            operationList[0].children().size() == 1
            with(operationList[0].'hudson.plugins.build__timeout.operations.WriteDescriptionOperation'[0]) {
                children().size() == 1
                description[0].value() == 'desc'
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'default timeout will fail the build'() {
        when:
        context.timeout {
            failBuild()
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].timeoutMinutes[0].value() == 3
            operationList[0].children().size() == 1
            operationList[0].children()[0].name() == 'hudson.plugins.build__timeout.operations.FailOperation'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'default timeout will abort the build'() {
        when:
        context.timeout {
            abortBuild()
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 1
            strategy[0].timeoutMinutes[0].value() == 3
            operationList[0].children().size() == 1
            operationList[0].children()[0].name() == 'hudson.plugins.build__timeout.operations.AbortOperation'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'likelyStuck timeout configuration working'() {
        when:
        context.timeout {
            likelyStuck()
        }

        then:
        with(context.wrapperNodes[0]) {
            children().size() == 2
            strategy[0].children().size() == 0
            strategy[0].@class == 'hudson.plugins.build_timeout.impl.LikelyStuckTimeOutStrategy'
            operationList[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('build-timeout', '1.12')
    }

    def 'port allocator string list'() {
        when:
        context.allocatePorts 'HTTP', '8080'

        then:
        context.wrapperNodes[0].name() == 'org.jvnet.hudson.plugins.port__allocator.PortAllocator'
        def ports = context.wrapperNodes[0].ports
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[0].name[0].value() == 'HTTP'
        ports.'org.jvnet.hudson.plugins.port__allocator.DefaultPortType'[1].name[0].value() == '8080'
        1 * mockJobManagement.requirePlugin('port-allocator')
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
        (1.._) * mockJobManagement.requirePlugin('port-allocator')
    }

    def 'sshAgent without credentials'() {
        when:
        context.sshAgent((String) null)

        then:
        thrown(DslScriptException)

        when:
        context.sshAgent((String[]) null)

        then:
        thrown(DslScriptException)
    }

    def 'sshAgent'() {
        when:
        context.sshAgent('acme')

        then:
        context.wrapperNodes[0].name() == 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper'
        context.wrapperNodes[0].user[0].value() == 'acme'
        1 * mockJobManagement.requirePlugin('ssh-agent')
        1 * mockJobManagement.logPluginDeprecationWarning('ssh-agent', '1.5')
    }

    def 'sshAgent with multiple credentials'() {
        when:
        context.sshAgent('acme', 'foo')

        then:
        context.wrapperNodes[0].name() == 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper'
        context.wrapperNodes[0].children().size() == 1
        context.wrapperNodes[0].credentialIds[0].children().size() == 2
        context.wrapperNodes[0].credentialIds[0].string[0].value() == 'acme'
        context.wrapperNodes[0].credentialIds[0].string[1].value() == 'foo'
        1 * mockJobManagement.requireMinimumPluginVersion('ssh-agent', '1.5')
    }

    def 'ansiColor with map'() {
        when:
        context.colorizeOutput('foo')

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.ansicolor.AnsiColorBuildWrapper'
        context.wrapperNodes[0].'colorMapName'[0].value() == 'foo'
        1 * mockJobManagement.requirePlugin('ansicolor')
    }

    def 'ansiColor without map should fall back to default xterm'() {
        when:
        context.colorizeOutput()

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.ansicolor.AnsiColorBuildWrapper'
        context.wrapperNodes[0].'colorMapName'[0].value() == 'xterm'
        1 * mockJobManagement.requirePlugin('ansicolor')
    }

    def 'xvnc'() {
        setup:
        mockJobManagement.isMinimumPluginVersionInstalled('xvnc', '1.16') >> true

        when:
        context.xvnc()

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.xvnc.Xvnc'
        def wrapper = context.wrapperNodes[0]
        wrapper.children().size() == 2
        wrapper.takeScreenshot[0].value() == false
        wrapper.useXauthority[0].value() == true
        1 * mockJobManagement.requirePlugin('xvnc')
    }

    def 'xvnc with closure'() {
        setup:
        mockJobManagement.isMinimumPluginVersionInstalled('xvnc', '1.16') >> true

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
        1 * mockJobManagement.requirePlugin('xvnc')
    }

    def 'xvnc with older plugin'() {
        when:
        context.xvnc()

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.xvnc.Xvnc'
        def wrapper = context.wrapperNodes[0]
        wrapper.children().size() == 1
        wrapper.takeScreenshot[0].value() == false
        1 * mockJobManagement.requirePlugin('xvnc')
    }

    def 'xvfb with minimal options'() {
        when:
        context.xvfb('default')

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.xvfb.XvfbBuildWrapper'
            children().size() == 8
            installationName[0].value() == 'default'
            screen[0].value() == '1024x768x24'
            debug[0].value() == false
            timeout[0].value() == 0
            displayNameOffset[0].value() == 1
            shutdownWithBuild[0].value() == false
            autoDisplayName[0].value() == false
            parallelBuild[0].value() == false
        }
        1 * mockJobManagement.requirePlugin('xvfb')
    }

    def 'xvfb with all options'() {
        when:
        context.xvfb('default') {
            screen('1920x1080x32')
            debug()
            timeout(500)
            displayNameOffset(24)
            shutdownWithBuild()
            autoDisplayName()
            assignedLabels('test')
            parallelBuild()
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.xvfb.XvfbBuildWrapper'
            children().size() == 9
            installationName[0].value() == 'default'
            screen[0].value() == '1920x1080x32'
            debug[0].value() == true
            timeout[0].value() == 500
            displayNameOffset[0].value() == 24
            shutdownWithBuild[0].value() == true
            autoDisplayName[0].value() == true
            assignedLabels[0].value() == 'test'
            parallelBuild[0].value() == true
        }
        1 * mockJobManagement.requirePlugin('xvfb')
    }

    def 'xvfb without installation'() {
        when:
        context.xvfb(installation)

        then:
        thrown(DslScriptException)

        where:
        installation << [null, '']
    }

    def 'toolenv'() {
        when:
        context.toolenv('Ant 1.8.2', 'Maven 3')

        then:
        context.wrapperNodes[0].name() == 'hudson.plugins.toolenv.ToolEnvBuildWrapper'
        context.wrapperNodes[0].'vars'[0].value() == 'ANT_1_8_2_HOME,MAVEN_3_HOME'
        1 * mockJobManagement.requirePlugin('toolenv')
    }

    def 'environmentVariables are added'() {
        when:
        context.environmentVariables {
            propertiesFile 'some.properties'
            envs test: 'some', other: 'any'
            env 'some', 'value'
            script 'echo Test'
            scriptFile '/var/lib/jenkins'
            groovy 'println "Hello"'
        }
        Node envNode = context.wrapperNodes[0]

        then:
        envNode.name() == 'EnvInjectBuildWrapper'
        def infoNode = envNode.info[0]
        infoNode.children().size() == 6
        infoNode.propertiesFilePath[0].value() == 'some.properties'
        infoNode.propertiesContent[0].value() == 'test=some\nother=any\nsome=value'
        infoNode.scriptFilePath[0].value() == '/var/lib/jenkins'
        infoNode.scriptContent[0].value() == 'echo Test'
        infoNode.groovyScriptContent[0].value() == 'println "Hello"'
        infoNode.loadFilesFromMaster[0].value() == false
        1 * mockJobManagement.requirePlugin('envinject')
    }

    def 'release plugin without options'() {
        when:
        context.release {
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.release.ReleaseWrapper'
            children().size() == 8
            releaseVersionTemplate[0].value() == ''
            doNotKeepLog[0].value() == false
            overrideBuildParameters[0].value() == false
            parameterDefinitions[0].children().size() == 0
            preBuildSteps[0].children().size() == 0
            postBuildSteps[0].children().size() == 0
            postSuccessfulBuildSteps[0].children().size() == 0
            postFailedBuildSteps[0].children().size() == 0
        }
        1 * mockJobManagement.requirePlugin('release')
    }

    def 'release plugin with all options'() {
        when:
        context.release {
            releaseVersionTemplate('templatename')
            doNotKeepLog()
            overrideBuildParameters()
            parameters {
                booleanParam('myBooleanParam', true)
                booleanParam('my2ndBooleanParam', true)
            }
            preBuildSteps {
                shell('echo hello;')
            }
            preBuildPublishers {
                archiveArtifacts('*.xml')
            }
            postSuccessfulBuildSteps {
                shell('echo postsuccess;')
                shell('echo hello world;')
            }
            postSuccessfulBuildPublishers {
                archiveArtifacts('*.xml')
            }
            postBuildSteps {
                shell('echo post;')
            }
            postBuildPublishers {
                archiveArtifacts('*.xml')
            }
            postFailedBuildSteps {
                shell('echo postfailed;')
            }
            postFailedBuildPublishers {
                archiveArtifacts('*.xml')
            }
            configure {
                it / custom('value')
            }
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.release.ReleaseWrapper'
            children().size() == 9
            releaseVersionTemplate[0].value() == 'templatename'
            doNotKeepLog[0].value() == true
            overrideBuildParameters[0].value() == true
            parameterDefinitions[0].children().size() == 2
            with(parameterDefinitions[0].children()[0]) {
                name() == 'hudson.model.BooleanParameterDefinition'
                children().size() == 2
                name[0].value() == 'myBooleanParam'
                defaultValue[0].value() == true
            }
            with(parameterDefinitions[0].children()[1]) {
                name() == 'hudson.model.BooleanParameterDefinition'
                children().size() == 2
                name[0].value() == 'my2ndBooleanParam'
                defaultValue[0].value() == true
            }
            preBuildSteps[0].children().size() == 2
            with(preBuildSteps[0].children()[0]) {
                name() == 'hudson.tasks.Shell'
                children().size() == 1
                command[0].value() == 'echo hello;'
            }
            with(preBuildSteps[0].children()[1]) {
                name() == 'hudson.tasks.ArtifactArchiver'
                children().size() == 5
            }
            postSuccessfulBuildSteps[0].children().size() == 3
            with(postSuccessfulBuildSteps[0].children()[0]) {
                name() == 'hudson.tasks.Shell'
                children().size() == 1
                command[0].value() == 'echo postsuccess;'
            }
            with(postSuccessfulBuildSteps[0].children()[1]) {
                name() == 'hudson.tasks.Shell'
                children().size() == 1
                command[0].value() == 'echo hello world;'
            }
            with(postSuccessfulBuildSteps[0].children()[2]) {
                name() == 'hudson.tasks.ArtifactArchiver'
                children().size() == 5
            }
            postBuildSteps[0].children().size() == 2
            with(postBuildSteps[0].children()[0]) {
                name() == 'hudson.tasks.Shell'
                children().size() == 1
                command[0].value() == 'echo post;'
            }
            with(postBuildSteps[0].children()[1]) {
                name() == 'hudson.tasks.ArtifactArchiver'
                children().size() == 5
            }
            postFailedBuildSteps[0].children().size() == 2
            with(postFailedBuildSteps[0].children()[0]) {
                name() == 'hudson.tasks.Shell'
                children().size() == 1
                command[0].value() == 'echo postfailed;'
            }
            with(postFailedBuildSteps[0].children()[1]) {
                name() == 'hudson.tasks.ArtifactArchiver'
                children().size() == 5
            }
            custom[0].value() == 'value'
        }
        1 * mockJobManagement.requirePlugin('release')
        4 * mockJobManagement.requireMinimumPluginVersion('release', '2.5.3')
    }

    def 'phabricator with minimal options'() {
        when:
        context.phabricator()

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.uber.jenkins.phabricator.PhabricatorBuildWrapper'
            children().size() == 3
            createCommit[0].value() == false
            applyToMaster[0].value() == false
            showBuildStartedMessage[0].value() == true
        }
        1 * mockJobManagement.requireMinimumPluginVersion('phabricator-plugin', '1.8.1')
    }

    def 'phabricator with all options'() {
        when:
        context.phabricator {
            createCommit()
            applyToMaster()
            showBuildStartedMessage(false)
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.uber.jenkins.phabricator.PhabricatorBuildWrapper'
            children().size() == 3
            createCommit[0].value() == true
            applyToMaster[0].value() == true
            showBuildStartedMessage[0].value() == false
        }
        1 * mockJobManagement.requireMinimumPluginVersion('phabricator-plugin', '1.8.1')
    }

    def 'call preBuildCleanup with minimal options'() {
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
        1 * mockJobManagement.requirePlugin('ws-cleanup')
    }

    def 'call preBuildCleanup with all options'() {
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
        1 * mockJobManagement.requirePlugin('ws-cleanup')
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
        1 * mockJobManagement.requirePlugin('logfilesizechecker')
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
        1 * mockJobManagement.requirePlugin('logfilesizechecker')
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
        1 * mockJobManagement.requirePlugin('logfilesizechecker')
    }

    def 'logSizeChecker with invalid maxSize'() {
        when:
        context.logSizeChecker {
            maxSize(-1)
        }

        then:
        thrown(DslScriptException)
    }

    def 'call injectPasswords with minimal args'() {
        when:
        context.injectPasswords {}

        then:
        with(context.wrapperNodes[0]) {
            name() == 'EnvInjectPasswordWrapper'
            children().size() == 3
            injectGlobalPasswords[0].value() == false
            maskPasswordParameters[0].value() == true
            passwordEntries[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('envinject', '1.90')
    }

    def 'call injectPasswords with all args'() {
        when:
        context.injectPasswords {
            injectGlobalPasswords()
            maskPasswordParameters(false)
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'EnvInjectPasswordWrapper'
            children().size() == 3
            injectGlobalPasswords[0].value() == true
            maskPasswordParameters[0].value() == false
            passwordEntries[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('envinject', '1.90')
    }

    def 'call buildName'() {
        when:
        context.buildName('#${BUILD_NUMBER} && <test>')

        then:
        context.wrapperNodes[0].name() == 'org.jenkinsci.plugins.buildnamesetter.BuildNameSetter'
        context.wrapperNodes[0].template[0].value() == '#${BUILD_NUMBER} && <test>'
        1 * mockJobManagement.requirePlugin('build-name-setter')
    }

    def 'call buildName with null parameter'() {
        when:
        context.buildName(null)

        then:
        thrown(DslScriptException)
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
        1 * mockJobManagement.requirePlugin('kpp-management-plugin')
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
        1 * mockJobManagement.requirePlugin('kpp-management-plugin')
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
        1 * mockJobManagement.requirePlugin('kpp-management-plugin')
    }

    def 'call configFile closure'() {
        setup:
        String configName = 'myCustomConfig'
        String configId = 'CustomConfig1417476679249'
        String configTarget = 'myTargetLocation'
        String configVariable = '$CONFIG_FILE_LOCATION'
        mockJobManagement.getConfigFileId(ConfigFileType.Custom, configName) >> configId

        when:
        context.configFiles {
            file(configName) {
                targetLocation configTarget
                variable configVariable
            }
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper'
            children().size() == 1
            managedFiles[0].children().size() == 1
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[0]) {
                children().size() == 3
                fileId[0].value() == configId
                targetLocation[0].value() == configTarget
                variable[0].value() == configVariable
            }
        }
        1 * mockJobManagement.requirePlugin('config-file-provider')
    }

    def 'call configFile'() {
        setup:
        String configName = 'myCustomConfig'
        String configId = 'CustomConfig1417476679249'
        mockJobManagement.getConfigFileId(ConfigFileType.Custom, configName) >> configId

        when:
        context.configFiles {
            file(configName)
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper'
            children().size() == 1
            managedFiles[0].children().size() == 1
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[0]) {
                children().size() == 3
                fileId[0].value() == configId
                targetLocation[0].value() == ''
                variable[0].value() == ''
            }
        }
        1 * mockJobManagement.requirePlugin('config-file-provider')
    }

    def 'call configFile with all file types'() {
        setup:
        String configName1 = 'myCustomConfig'
        String configId1 = 'CustomConfig1417476679249'
        String configName2 = 'myOtherConfig'
        String configId2 = 'CustomConfig1417476679250'
        String configName3 = 'myMavenSetttings'
        String configId3 = 'CustomConfig1417476679251'
        String configName4 = 'myGlobalMavenSetttings'
        String configId4 = 'CustomConfig1417476679252'
        mockJobManagement.getConfigFileId(ConfigFileType.Custom, configName1) >> configId1
        mockJobManagement.getConfigFileId(ConfigFileType.Custom, configName2) >> configId2
        mockJobManagement.getConfigFileId(ConfigFileType.MavenSettings, configName3) >> configId3
        mockJobManagement.getConfigFileId(ConfigFileType.GlobalMavenSettings, configName4) >> configId4

        when:
        context.configFiles {
            file(configName1)
            custom(configName2)
            mavenSettings(configName3)
            globalMavenSettings(configName4)
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper'
            children().size() == 1
            managedFiles[0].children().size() == 4
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[0]) {
                children().size() == 3
                fileId[0].value() == configId1
                targetLocation[0].value() == ''
                variable[0].value() == ''
            }
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[1]) {
                children().size() == 3
                fileId[0].value() == configId2
                targetLocation[0].value() == ''
                variable[0].value() == ''
            }
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[2]) {
                children().size() == 3
                fileId[0].value() == configId3
                targetLocation[0].value() == ''
                variable[0].value() == ''
            }
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[3]) {
                children().size() == 3
                fileId[0].value() == configId4
                targetLocation[0].value() == ''
                variable[0].value() == ''
            }
        }
        1 * mockJobManagement.requirePlugin('config-file-provider')
    }

    def 'call configFile with file ID'() {
        setup:
        String configId = 'lala'

        when:
        context.configFiles {
            file(configId)
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper'
            children().size() == 1
            managedFiles[0].children().size() == 1
            with(managedFiles[0].'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile'[0]) {
                children().size() == 3
                fileId[0].value() == configId
                targetLocation[0].value() == ''
                variable[0].value() == ''
            }
        }
        1 * mockJobManagement.requirePlugin('config-file-provider')
    }

    def 'call exclusion with single arg'() {
        when:
        context.exclusionResources('first')

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.exclusion.IdAllocator'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[0].name[0].value() == 'FIRST'
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('Exclusion', '0.12')
    }

    def 'call exclusion with multiple args'() {
        when:
        context.exclusionResources(['first', 'second', 'third'])

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.exclusion.IdAllocator'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[0].name[0].value() == 'FIRST'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[1].name[0].value() == 'SECOND'
            ids[0].'org.jvnet.hudson.plugins.exclusion.DefaultIdType'[2].name[0].value() == 'THIRD'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('Exclusion', '0.12')
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
        1 * mockJobManagement.requirePlugin('delivery-pipeline-plugin')
        1 * mockJobManagement.logPluginDeprecationWarning('delivery-pipeline-plugin', '0.10.0')
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
        1 * mockJobManagement.requirePlugin('delivery-pipeline-plugin')
        1 * mockJobManagement.logPluginDeprecationWarning('delivery-pipeline-plugin', '0.10.0')
    }

    def 'call mask passwords'() {
        when:
        context.maskPasswords()

        then:
        context.wrapperNodes.size() == 1
        context.wrapperNodes[0].name() == 'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'
        1 * mockJobManagement.requirePlugin('mask-passwords')
    }

    def 'call build user vars'() {
        when:
        context.buildUserVars()

        then:
        context.wrapperNodes.size() == 1
        context.wrapperNodes[0].name() == 'org.jenkinsci.plugins.builduser.BuildUser'
        1 * mockJobManagement.requirePlugin('build-user-vars-plugin')
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
        1 * mockJobManagement.requirePlugin('nodejs')
    }

    def 'call sauce on demand with defaults for older plugin version'() {
        when:
        context.sauceOnDemand {
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.sauce__ondemand.SauceOnDemandBuildWrapper'
            children().size() == 15
            useGeneratedTunnelIdentifier[0].value() == false
            sendUsageData[0].value() == false
            nativeAppPackage[0].value() == ''
            useChromeForAndroid[0].value() == false
            sauceConnectPath[0].value() == ''
            enableSauceConnect[0].value() == false
            seleniumHost[0].value() == ''
            seleniumPort[0].value() == ''
            webDriverBrowsers[0].value().empty
            appiumBrowsers[0].value().empty
            useLatestVersion[0].value() == false
            launchSauceConnectOnSlave[0].value() == false
            options[0].value() == ''
            verboseLogging[0].value() == false
            condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
        }
    }

    def 'call sauce on demand with defaults'() {
        given:
        mockJobManagement.isMinimumPluginVersionInstalled('sauce-ondemand', '1.148') >> true

        when:
        context.sauceOnDemand {
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.sauce__ondemand.SauceOnDemandBuildWrapper'
            children().size() == 16
            useGeneratedTunnelIdentifier[0].value() == false
            sendUsageData[0].value() == false
            nativeAppPackage[0].value() == ''
            useChromeForAndroid[0].value() == false
            sauceConnectPath[0].value() == ''
            enableSauceConnect[0].value() == false
            seleniumHost[0].value() == ''
            seleniumPort[0].value() == ''
            webDriverBrowsers[0].value().empty
            appiumBrowsers[0].value().empty
            useLatestVersion[0].value() == false
            launchSauceConnectOnSlave[0].value() == false
            options[0].value() == ''
            credentialId[0].value() == ''
            verboseLogging[0].value() == false
            condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
        }
    }

    def 'call sauce on demand with all options'() {
        given:
        mockJobManagement.isMinimumPluginVersionInstalled('sauce-ondemand', '1.148') >> true

        when:
        context.sauceOnDemand {
            useGeneratedTunnelIdentifier()
            sendUsageData()
            nativeAppPackage('nativeAppPackage')
            sauceConnectPath('sauceConnectPath')
            enableSauceConnect()
            seleniumHost('seleniumHost')
            seleniumPort('seleniumPort')
            webDriverBrowsers('foo', 'bar')
            webDriverBrowsers('test')
            appiumBrowsers('larry', 'curly')
            appiumBrowsers('moe')
            useLatestVersion()
            launchSauceConnectOnSlave()
            options('options')
            credentials('credentialId')
            verboseLogging()
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.sauce__ondemand.SauceOnDemandBuildWrapper'
            children().size() == 16
            useGeneratedTunnelIdentifier[0].value() == true
            sendUsageData[0].value() == true
            nativeAppPackage[0].value() == 'nativeAppPackage'
            useChromeForAndroid[0].value() == false
            sauceConnectPath[0].value() == 'sauceConnectPath'
            enableSauceConnect[0].value() == true
            seleniumHost[0].value() == 'seleniumHost'
            seleniumPort[0].value() == 'seleniumPort'
            webDriverBrowsers[0].string.any { it.value() == 'foo' }
            webDriverBrowsers[0].string.any { it.value() == 'bar' }
            webDriverBrowsers[0].string.any { it.value() == 'test' }
            webDriverBrowsers[0].string.size() == 3
            appiumBrowsers[0].string.any { it.value() == 'larry' }
            appiumBrowsers[0].string.any { it.value() == 'curly' }
            appiumBrowsers[0].string.any { it.value() == 'moe' }
            appiumBrowsers[0].string.size() == 3
            useLatestVersion[0].value() == true
            launchSauceConnectOnSlave[0].value() == true
            options[0].value() == 'options'
            credentialId[0].value() == 'credentialId'
            verboseLogging[0].value() == true
            condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
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
        1 * mockJobManagement.requirePlugin('golang')
    }

    def 'call credentials binding'() {
        when:
        context.credentialsBinding {
            file('A', 'foo')
            string('B', 'bar')
            usernamePassword('C', 'baz')
            zipFile('D', 'foobar')
        }

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper'
            children().size() == 1
            bindings[0].children().size() == 4
            with(bindings[0].'org.jenkinsci.plugins.credentialsbinding.impl.FileBinding'[0]) {
                children().size() == 2
                variable[0].value() == 'A'
                credentialsId[0].value() == 'foo'
            }
            with(bindings[0].'org.jenkinsci.plugins.credentialsbinding.impl.StringBinding'[0]) {
                children().size() == 2
                variable[0].value() == 'B'
                credentialsId[0].value() == 'bar'
            }
            with(bindings[0].'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordBinding'[0]) {
                children().size() == 2
                variable[0].value() == 'C'
                credentialsId[0].value() == 'baz'
            }
            with(bindings[0].'org.jenkinsci.plugins.credentialsbinding.impl.ZipFileBinding'[0]) {
                children().size() == 2
                variable[0].value() == 'D'
                credentialsId[0].value() == 'foobar'
            }
        }
        1 * mockJobManagement.requirePlugin('credentials-binding')
    }

    def 'call credentials binding with username password multi binding'() {
        when:
        context.credentialsBinding {
            usernamePassword('A', 'B', 'foo')
        }

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper'
            children().size() == 1
            bindings[0].children().size() == 1
            with(bindings[0].'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordMultiBinding'[0]) {
                children().size() == 3
                usernameVariable[0].value() == 'A'
                passwordVariable[0].value() == 'B'
                credentialsId[0].value() == 'foo'
            }
        }
        1 * mockJobManagement.requirePlugin('credentials-binding')
        1 * mockJobManagement.requireMinimumPluginVersion('credentials-binding', '1.3')
    }

    def 'call custom tools with no optionals'() {
        when:
        context.customTools(['foo', 'bar'])

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper'
            children().size() == 3
            convertHomesToUppercase[0].value() == false
            multiconfigOptions[0].skipMasterInstallation[0].value() == false
            def tools = selectedTools[0].
                    'com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool'
            with(tools[0]) {
                children().size() == 1
                name[0].value() == 'foo'
            }
            with(tools[1]) {
                children().size() == 1
                name[0].value() == 'bar'
            }
        }
        1 * mockJobManagement.requirePlugin('custom-tools-plugin')
    }

    def 'call custom tools with closure'() {
        when:
        context.customTools(['bar', 'baz']) {
            convertHomesToUppercase()
            skipMasterInstallation()
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper'
            children().size() == 3
            convertHomesToUppercase[0].value() == true
            multiconfigOptions[0].skipMasterInstallation[0].value() == true
            def tools = selectedTools[0].
                    'com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool'
            with(tools[0]) {
                children().size() == 1
                name[0].value() == 'bar'
            }
            with(tools[1]) {
                children().size() == 1
                name[0].value() == 'baz'
            }
        }
        1 * mockJobManagement.requirePlugin('custom-tools-plugin')
    }

    def 'pre SCM build steps with minimal options'() {
        when:
        context.preScmSteps {
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.preSCMbuildstep.PreSCMBuildStepsWrapper'
            children().size() == 2
            buildSteps[0].children().size() == 0
            failOnError[0].value() == false
        }
        1 * mockJobManagement.requirePlugin('preSCMbuildstep')
    }

    def 'pre SCM build steps with all options'() {
        when:
        context.preScmSteps {
            steps {
                shell('echo HELLO')
                batchFile('echo WORLD')
            }
            failOnError()
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'org.jenkinsci.plugins.preSCMbuildstep.PreSCMBuildStepsWrapper'
            children().size() == 2
            buildSteps[0].children().size() == 2
            buildSteps[0].children()[0].name() == 'hudson.tasks.Shell'
            buildSteps[0].children()[1].name() == 'hudson.tasks.BatchFile'
            failOnError[0].value() == true
        }
        1 * mockJobManagement.requirePlugin('preSCMbuildstep')
    }

    def 'buildInDocker with no options'() {
        when:
        context.buildInDocker {
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.cloudbees.jenkins.plugins.okidocki.DockerBuildWrapper'
            children().size() == 9
            selector[0].children().size() == 2
            selector[0].attribute('class') == 'com.cloudbees.jenkins.plugins.okidocki.DockerfileImageSelector'
            selector[0].contextPath[0].value() == '.'
            selector[0].dockerfile[0].value() == 'Dockerfile'
            dockerHost[0].value().empty
            dockerRegistryCredentials[0].value().empty
            verbose[0].value() == false
            volumes[0].value().empty
            privileged[0].value() == false
            forcePull[0].value() == false
            group[0].value().empty
            command[0].value() == '/bin/cat'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('docker-custom-build-environment', '1.6.2')
    }

    def 'buildInDocker with dockerfile selector and all options'() {
        when:
        context.buildInDocker {
            dockerfile('test1', 'test2')
            dockerHostURI('test3')
            serverCredentials('test4')
            registryCredentials('test5')
            volume('test6', 'test7')
            volume('test8', 'test9')
            privilegedMode()
            forcePull()
            verbose()
            userGroup('test10')
            startCommand('test11')
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.cloudbees.jenkins.plugins.okidocki.DockerBuildWrapper'
            children().size() == 9
            selector[0].children().size() == 2
            selector[0].attribute('class') == 'com.cloudbees.jenkins.plugins.okidocki.DockerfileImageSelector'
            selector[0].contextPath[0].value() == 'test1'
            selector[0].dockerfile[0].value() == 'test2'
            dockerHost[0].children().size() == 2
            dockerHost[0].uri[0].value() == 'test3'
            dockerHost[0].credentialsId[0].value() == 'test4'
            dockerRegistryCredentials[0].value() == 'test5'
            verbose[0].value() == true
            volumes[0].children().size() == 2
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[0].children().size() == 2
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[0].hostPath[0].value() == 'test6'
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[0].path[0].value() == 'test7'
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[1].children().size() == 2
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[1].hostPath[0].value() == 'test8'
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[1].path[0].value() == 'test9'
            privileged[0].value() == true
            forcePull[0].value() == true
            group[0].value() == 'test10'
            command[0].value() == 'test11'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('docker-custom-build-environment', '1.6.2')
    }

    def 'buildInDocker with image selector and all options'() {
        when:
        context.buildInDocker {
            image('test1')
            dockerHostURI('test3')
            serverCredentials('test4')
            registryCredentials('test5')
            volume('test6', 'test7')
            volume('test8', 'test9')
            privilegedMode()
            forcePull()
            verbose()
            userGroup('test10')
            startCommand('test11')
        }

        then:
        with(context.wrapperNodes[0]) {
            name() == 'com.cloudbees.jenkins.plugins.okidocki.DockerBuildWrapper'
            children().size() == 9
            selector[0].children().size() == 1
            selector[0].attribute('class') == 'com.cloudbees.jenkins.plugins.okidocki.PullDockerImageSelector'
            selector[0].image[0].value() == 'test1'
            dockerHost[0].children().size() == 2
            dockerHost[0].uri[0].value() == 'test3'
            dockerHost[0].credentialsId[0].value() == 'test4'
            dockerRegistryCredentials[0].value() == 'test5'
            verbose[0].value() == true
            volumes[0].children().size() == 2
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[0].children().size() == 2
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[0].hostPath[0].value() == 'test6'
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[0].path[0].value() == 'test7'
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[1].children().size() == 2
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[1].hostPath[0].value() == 'test8'
            volumes[0].'com.cloudbees.jenkins.plugins.okidocki.Volume'[1].path[0].value() == 'test9'
            privileged[0].value() == true
            forcePull[0].value() == true
            group[0].value() == 'test10'
            command[0].value() == 'test11'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('docker-custom-build-environment', '1.6.2')
    }

    def 'call generateJiraReleaseNotes with no options'() {
        when:
        context.generateJiraReleaseNotes {
        }

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.jira.JiraCreateReleaseNotes'
            children().size() == 4
            jiraEnvironmentVariable[0].value().empty
            jiraProjectKey[0].value().empty
            jiraRelease[0].value().empty
            jiraFilter[0].value().empty
        }
        1 * mockJobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call generateJiraReleaseNotes with all options'() {
        when:
        context.generateJiraReleaseNotes {
            environmentVariable(env)
            projectKey(key)
            delegate.release(release)
            delegate.filter(filter)
        }

        then:
        context.wrapperNodes.size() == 1
        with(context.wrapperNodes[0]) {
            name() == 'hudson.plugins.jira.JiraCreateReleaseNotes'
            children().size() == 4
            jiraEnvironmentVariable[0].value() == expectedEnv
            jiraProjectKey[0].value() == expectedKey
            jiraRelease[0].value() == expectedRel
            jiraFilter[0].value() == expectedFil
        }
        1 * mockJobManagement.requireMinimumPluginVersion('jira', '1.39')

        where:
        env    | key    | release | filter | expectedEnv | expectedKey | expectedRel | expectedFil
        null   | null   | null    | null   | ''          | ''          | ''          | ''
        'key1' | null   | null    | null   | 'key1'      | ''          | ''          | ''
        'key1' | 'key2' | null    | null   | 'key1'      | 'key2'      | ''          | ''
        'key1' | 'key2' | 'key3'  | null   | 'key1'      | 'key2'      | 'key3'      | ''
        'key1' | 'key2' | 'key3'  | 'key4' | 'key1'      | 'key2'      | 'key3'      | 'key4'
    }
}
