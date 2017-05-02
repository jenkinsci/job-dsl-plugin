package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation
import spock.lang.Specification
import spock.lang.Unroll

class StepContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    StepContext context = new StepContext(jobManagement, item)

    def 'call shell method'() {
        when:
        context.shell('echo "Hello"')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def shellStep = context.stepNodes[0]
        shellStep.name() == 'hudson.tasks.Shell'
        shellStep.command[0].value() == 'echo "Hello"'
    }

    def 'call remoteShell method with minimal options'() {
        when:
        context.remoteShell('root@example.com:22') {
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.SSHBuilder'
            children().size() == 2
            siteName[0].value() == 'root@example.com:22'
            command[0].value() == ''
        }
        1 * jobManagement.requireMinimumPluginVersion('ssh', '1.3')
    }

    def 'call remoteShell method with all options'() {
        when:
        context.remoteShell('root@example.com:22') {
            command('echo Hello', 'echo World!')
            command('echo How are you?')
            command(["echo I'm fine!", 'echo And you?'])
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.SSHBuilder'
            children().size() == 2
            siteName[0].value() == 'root@example.com:22'
            command[0].value() == "echo Hello\necho World!\necho How are you?\necho I'm fine!\necho And you?"
        }
        1 * jobManagement.requireMinimumPluginVersion('ssh', '1.3')
    }

    def 'call xshell method with defaults'() {
        when:
        context.xShell()

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.xshell.XShellBuilder'
            children().size() == 3
            commandLine[0].value() == ''
            executeFromWorkingDir[0].value() == false
            regexToKill[0].value() == ''
        }
        1 * jobManagement.requireMinimumPluginVersion('xshell', '0.10')
    }

    def 'call xshell method with all options'() {
        when:
        context.xShell {
            commandLine('echo "Hello"')
            executableInWorkspaceDir()
            regexToKill('.*regexp.*')
            timeAllocated(10)
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.xshell.XShellBuilder'
            children().size() == 4
            commandLine[0].value() == 'echo "Hello"'
            executeFromWorkingDir[0].value() == true
            regexToKill[0].value() == '.*regexp.*'
            timeAllocated[0].value() == 10
        }
        1 * jobManagement.requireMinimumPluginVersion('xshell', '0.10')
    }

    def 'call batchFile method'() {
        when:
        context.batchFile('echo "Hello from Windows"')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def shellStep = context.stepNodes[0]
        shellStep.name() == 'hudson.tasks.BatchFile'
        shellStep.command[0].value() == 'echo "Hello from Windows"'
    }

    def 'call powerShell method'() {
        when:
        context.powerShell('New-Item c:\\testBuilds')

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.powershell.PowerShell'
            children().size() == 1
            command[0].value() == 'New-Item c:\\testBuilds'
        }
        1 * jobManagement.requireMinimumPluginVersion('powershell', '1.2')
    }

    def 'call buildDescription method with all options'() {
        when:
        context.buildDescription('[version] (.*)', 'foo \\1')

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.descriptionsetter.DescriptionSetterBuilder'
            children().size() == 2
            regexp[0].value() == '[version] (.*)'
            description[0].value() == 'foo \\1'
        }
        1 * jobManagement.requireMinimumPluginVersion('description-setter', '1.9')
    }

    def 'call buildDescription method with minimum options'() {
        when:
        context.buildDescription('[version] (.*)')

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.descriptionsetter.DescriptionSetterBuilder'
            children().size() == 2
            regexp[0].value() == '[version] (.*)'
            description[0].value() == ''
        }
        1 * jobManagement.requireMinimumPluginVersion('description-setter', '1.9')
    }

    def 'call gradle methods'() {
        when:
        context.gradle('build')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def gradleStep = context.stepNodes[0]
        gradleStep.name() == 'hudson.plugins.gradle.Gradle'
        gradleStep.tasks[0].value() == 'build'
        gradleStep.useWrapper[0].value() == true
        (1.._) * jobManagement.requireMinimumPluginVersion('gradle', '1.23')

        when:
        context.gradle('build', '-I init.gradle', false)

        then:
        context.stepNodes.size() == 2
        def gradleStep2 = context.stepNodes[1]
        gradleStep2.switches[0].value() == '-I init.gradle'
        gradleStep2.useWrapper[0].value() == false
        (1.._) * jobManagement.requireMinimumPluginVersion('gradle', '1.23')

        when:
        context.gradle('build', '-I init.gradle', false) {
            it / 'node1' << 'value1'
        }

        then:
        context.stepNodes.size() == 3
        def gradleStep3 = context.stepNodes[2]
        gradleStep3.node1[0].value() == 'value1'
        (1.._) * jobManagement.requireMinimumPluginVersion('gradle', '1.23')
    }

    def 'call gradle methods with defaults'() {
        when:
        context.gradle()

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            children().size() == 10
            tasks[0].value() == ''
            switches[0].value() == ''
            useWrapper[0].value() == true
            description[0].value() == ''
            rootBuildScriptDir[0].value() == ''
            buildFile[0].value() == ''
            gradleName[0].value() == '(Default)'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == false
            useWorkspaceAsHome[0].value() == false
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('gradle', '1.23')

        when:
        context.gradle {
        }

        then:
        context.stepNodes.size() == 2
        with(context.stepNodes[1]) {
            children().size() == 10
            tasks[0].value() == ''
            switches[0].value() == ''
            useWrapper[0].value() == true
            description[0].value() == ''
            rootBuildScriptDir[0].value() == ''
            buildFile[0].value() == ''
            gradleName[0].value() == '(Default)'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == false
            useWorkspaceAsHome[0].value() == false
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('gradle', '1.23')
    }

    def 'call gradle methods with context and old plugin version'() {
        when:
        context.gradle {
            tasks 'clean'
            tasks 'build'
            switches '--info'
            switches '--stacktrace'
            useWrapper false
            description 'desc'
            rootBuildScriptDir 'rbsd'
            buildFile 'bf'
            gradleName 'gn'
            fromRootBuildScriptDir true
            makeExecutable true
            useWorkspaceAsHome true
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            children().size() == 10
            tasks[0].value() == 'clean build'
            switches[0].value() == '--info --stacktrace'
            useWrapper[0].value() == false
            description[0].value() == 'desc'
            rootBuildScriptDir[0].value() == 'rbsd'
            buildFile[0].value() == 'bf'
            gradleName[0].value() == 'gn'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == true
            useWorkspaceAsHome[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('gradle', '1.23')
    }

    def 'call gradle methods with context'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('gradle', '1.25') >> true

        when:
        context.gradle {
            tasks 'clean'
            tasks 'build'
            switches '--info'
            switches '--stacktrace'
            useWrapper false
            description 'desc'
            rootBuildScriptDir 'rbsd'
            buildFile 'bf'
            gradleName 'gn'
            fromRootBuildScriptDir true
            makeExecutable true
            useWorkspaceAsHome true
            passAsProperties true
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            children().size() == 11
            tasks[0].value() == 'clean build'
            switches[0].value() == '--info --stacktrace'
            useWrapper[0].value() == false
            description[0].value() == 'desc'
            rootBuildScriptDir[0].value() == 'rbsd'
            buildFile[0].value() == 'bf'
            gradleName[0].value() == 'gn'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == true
            useWorkspaceAsHome[0].value() == true
            passAsProperties[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('gradle', '1.23')
        1 * jobManagement.requireMinimumPluginVersion('gradle', '1.25')
    }

    def 'call grails methods'() {
        when:
        context.grails('compile')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def grailsStep0 = context.stepNodes[0]
        grailsStep0.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep0.targets[0].value() == 'compile'
        grailsStep0.useWrapper[0].value() == false
        grailsStep0.grailsWorkDir[0].value() == ''
        grailsStep0.projectWorkDir[0].value() == ''
        grailsStep0.projectBaseDir[0].value() == ''
        grailsStep0.serverPort[0].value() == ''
        grailsStep0.'properties'[0].value() == ''
        grailsStep0.forceUpgrade[0].value() == false
        grailsStep0.nonInteractive[0].value() == true
        (1.._) * jobManagement.requirePlugin('grails')
        1 * jobManagement.logDeprecationWarning()

        when:
        context.grails('compile', true)

        then:
        context.stepNodes.size() == 2
        def grailsStep1 = context.stepNodes[1]
        grailsStep1.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep1.targets[0].value() == 'compile'
        grailsStep1.useWrapper[0].value() == true
        grailsStep1.grailsWorkDir[0].value() == ''
        grailsStep1.projectWorkDir[0].value() == ''
        grailsStep1.projectBaseDir[0].value() == ''
        grailsStep1.serverPort[0].value() == ''
        grailsStep1.'properties'[0].value() == ''
        grailsStep1.forceUpgrade[0].value() == false
        grailsStep1.nonInteractive[0].value() == true
        (1.._) * jobManagement.requirePlugin('grails')
        1 * jobManagement.logDeprecationWarning()

        when:
        context.grails('compile', false) {
            grailsWorkDir 'work1'
            nonInteractive false
        }

        then:
        context.stepNodes.size() == 3
        def grailsStep2 = context.stepNodes[2]
        grailsStep2.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep2.targets[0].value() == 'compile'
        grailsStep2.useWrapper[0].value() == false
        grailsStep2.grailsWorkDir[0].value() == 'work1'
        grailsStep2.projectWorkDir[0].value() == ''
        grailsStep2.projectBaseDir[0].value() == ''
        grailsStep2.serverPort[0].value() == ''
        grailsStep2.'properties'[0].value() == ''
        grailsStep2.forceUpgrade[0].value() == false
        grailsStep2.nonInteractive[0].value() == false
        (1.._) * jobManagement.requirePlugin('grails')
        1 * jobManagement.logDeprecationWarning()

        when:
        context.grails {
            target 'clean'
            targets(['compile', 'test-app'])
            useWrapper true
            grailsWorkDir 'work'
            projectWorkDir 'project'
            projectBaseDir 'base'
            serverPort '1111'
            props prop1: 'val1', prop2: 'val2'
            prop 'prop3', 'val3'
            forceUpgrade true
            nonInteractive false
        }

        then:
        context.stepNodes.size() == 4
        def grailsStep3 = context.stepNodes[3]
        grailsStep3.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep3.targets[0].value() == 'clean compile test-app'
        grailsStep3.useWrapper[0].value() == true
        grailsStep3.grailsWorkDir[0].value() == 'work'
        grailsStep3.projectWorkDir[0].value() == 'project'
        grailsStep3.projectBaseDir[0].value() == 'base'
        grailsStep3.serverPort[0].value() == '1111'
        grailsStep3.'properties'[0].value() == 'prop1=val1\nprop2=val2\nprop3=val3'
        grailsStep3.forceUpgrade[0].value() == true
        grailsStep3.nonInteractive[0].value() == false
        (1.._) * jobManagement.requirePlugin('grails')
        (1.._) * jobManagement.logDeprecationWarning()

        when:
        context.grails '"test-app --stacktrace"', {
            useWrapper true
            grailsWorkDir 'work'
            projectWorkDir 'project'
            projectBaseDir 'base'
            serverPort '8080'
            forceUpgrade true
            nonInteractive false
        }

        then:
        context.stepNodes.size() == 5
        def grailsStep4 = context.stepNodes[4]
        grailsStep4.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep4.targets[0].value() == '"test-app --stacktrace"'
        grailsStep4.useWrapper[0].value() == true
        grailsStep4.grailsWorkDir[0].value() == 'work'
        grailsStep4.projectWorkDir[0].value() == 'project'
        grailsStep4.projectBaseDir[0].value() == 'base'
        grailsStep4.serverPort[0].value() == '8080'
        grailsStep4.'properties'[0].value() == ''
        grailsStep4.forceUpgrade[0].value() == true
        grailsStep4.nonInteractive[0].value() == false
        (1.._) * jobManagement.requirePlugin('grails')
        (1.._) * jobManagement.logDeprecationWarning()

        when:
        context.grails {
        }

        then:
        context.stepNodes.size() == 6
        def grailsStep5 = context.stepNodes[5]
        grailsStep5.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep5.targets[0].value() == ''
        grailsStep5.useWrapper[0].value() == false
        grailsStep5.grailsWorkDir[0].value() == ''
        grailsStep5.projectWorkDir[0].value() == ''
        grailsStep5.projectBaseDir[0].value() == ''
        grailsStep5.serverPort[0].value() == ''
        grailsStep5.'properties'[0].value() == ''
        grailsStep5.forceUpgrade[0].value() == false
        grailsStep5.nonInteractive[0].value() == true
        (1.._) * jobManagement.requirePlugin('grails')
        (1.._) * jobManagement.logDeprecationWarning()
    }

    def 'call maven methods'() {
        when:
        context.maven('install')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def mavenStep = context.stepNodes[0]
        mavenStep.name() == 'hudson.tasks.Maven'
        mavenStep.targets[0].value() == 'install'
        mavenStep.pom[0] == null

        when:
        context.maven('install', 'pom.xml') { mavenNode ->
            def nameNode = mavenNode / mavenName
            nameNode.value = 'Maven 2.0.1'
        }

        then:
        context.stepNodes.size() == 2
        def mavenStep2 = context.stepNodes[1]
        mavenStep2.pom[0].value() == 'pom.xml'
        mavenStep2.mavenName[0].value() == 'Maven 2.0.1'
    }

    def 'call maven method with full context'() {
        when:
        context.maven {
            rootPOM('module-a/pom.xml')
            goals('clean')
            goals('install')
            mavenOpts('-Xms256m')
            mavenOpts('-Xmx512m')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            mavenInstallation('Maven 3.0.5')
            properties skipTests: true, other: 'some'
            property 'evenAnother', 'One'
            configure {
                it / settingsConfigId('foo-bar')
            }
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def mavenStep = context.stepNodes[0]
        mavenStep.name() == 'hudson.tasks.Maven'
        mavenStep.children().size() == 7
        mavenStep.targets[0].value() == 'clean install'
        mavenStep.pom[0].value() == 'module-a/pom.xml'
        mavenStep.jvmOptions[0].value() == '-Xms256m -Xmx512m'
        mavenStep.usePrivateRepository[0].value() == true
        mavenStep.mavenName[0].value() == 'Maven 3.0.5'
        mavenStep.settingsConfigId[0].value() == 'foo-bar'
        mavenStep.properties[0].value() == 'skipTests=true\nother=some\nevenAnother=One'
    }

    def 'call maven method with full context and core version >= 2.12'() {
        setup:
        jobManagement.isMinimumCoreVersion('2.12') >> true

        when:
        context.maven {
            rootPOM('module-a/pom.xml')
            goals('clean')
            goals('install')
            mavenOpts('-Xms256m')
            mavenOpts('-Xmx512m')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            mavenInstallation('Maven 3.0.5')
            properties(skipTests: true, other: 'some')
            property('evenAnother', 'One')
            injectBuildVariables(false)
            configure {
                it / settingsConfigId('foo-bar')
            }
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def mavenStep = context.stepNodes[0]
        mavenStep.name() == 'hudson.tasks.Maven'
        mavenStep.children().size() == 8
        mavenStep.targets[0].value() == 'clean install'
        mavenStep.pom[0].value() == 'module-a/pom.xml'
        mavenStep.jvmOptions[0].value() == '-Xms256m -Xmx512m'
        mavenStep.usePrivateRepository[0].value() == true
        mavenStep.mavenName[0].value() == 'Maven 3.0.5'
        mavenStep.settingsConfigId[0].value() == 'foo-bar'
        mavenStep.properties[0].value() == 'skipTests=true\nother=some\nevenAnother=One'
        mavenStep.injectBuildVariables[0].value() == false
        1 * jobManagement.requireMinimumCoreVersion('2.12')
    }

    def 'call maven method with minimal context'() {
        when:
        context.maven {
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def mavenStep = context.stepNodes[0]
        mavenStep.name() == 'hudson.tasks.Maven'
        mavenStep.children().size() == 4
        mavenStep.targets[0].value() == ''
        mavenStep.jvmOptions[0].value() == ''
        mavenStep.usePrivateRepository[0].value() == false
        mavenStep.mavenName[0].value() == '(Default)'
    }

    def 'call maven method with minimal context and core version >= 2.12'() {
        setup:
        jobManagement.isMinimumCoreVersion('2.12') >> true

        when:
        context.maven {
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def mavenStep = context.stepNodes[0]
        mavenStep.name() == 'hudson.tasks.Maven'
        mavenStep.children().size() == 5
        mavenStep.targets[0].value() == ''
        mavenStep.jvmOptions[0].value() == ''
        mavenStep.usePrivateRepository[0].value() == false
        mavenStep.mavenName[0].value() == '(Default)'
        mavenStep.injectBuildVariables[0].value() == true
    }

    def 'call maven method with provided settings ID'() {
        setup:
        String settingsId = 'lalala'

        when:
        context.maven {
            providedSettings(settingsId)
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.tasks.Maven'
            children().size() == 5
            targets[0].value() == ''
            jvmOptions[0].value() == ''
            usePrivateRepository[0].value() == false
            mavenName[0].value() == '(Default)'
            with(settings[0]) {
                attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
                children().size() == 1
                settingsConfigId[0].value() == settingsId
            }
        }
    }

    def 'call maven method with provided global settings ID'() {
        setup:
        String settingsId = 'lalala'

        when:
        context.maven {
            providedGlobalSettings(settingsId)
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.tasks.Maven'
            children().size() == 5
            targets[0].value() == ''
            jvmOptions[0].value() == ''
            usePrivateRepository[0].value() == false
            mavenName[0].value() == '(Default)'
            with(globalSettings[0]) {
                attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnGlobalSettingsProvider'
                children().size() == 1
                settingsConfigId[0].value() == settingsId
            }
        }
    }

    def 'call maven method with provided settings names'() {
        setup:
        String settingsName = 'maven-proxy'
        String settingsId = '123123415'
        String globalSettingsName = 'maven-global'
        String globalSettingsId = '123123416'
        jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName) >> settingsId
        jobManagement.getConfigFileId(ConfigFileType.GlobalMavenSettings, globalSettingsName) >> globalSettingsId

        when:
        context.maven {
            providedSettings(settingsName)
            providedGlobalSettings(globalSettingsName)
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.tasks.Maven'
            children().size() == 6
            targets[0].value() == ''
            jvmOptions[0].value() == ''
            usePrivateRepository[0].value() == false
            mavenName[0].value() == '(Default)'
            with(settings[0]) {
                attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
                children().size() == 1
                settingsConfigId[0].value() == settingsId
            }
            with(globalSettings[0]) {
                attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnGlobalSettingsProvider'
                children().size() == 1
                settingsConfigId[0].value() == globalSettingsId
            }
        }
    }

    def 'call ant methods'() {
        when:
        context.ant()

        then:
        context.stepNodes.size() == 1
        def antEmptyNode = context.stepNodes[0]
        antEmptyNode.name() == 'hudson.tasks.Ant'
        antEmptyNode.targets[0].value() == ''
        antEmptyNode.antName[0].value() == '(Default)'
        !antEmptyNode.children().any { it.name() == 'antOpts' }
        !antEmptyNode.children().any { it.name() == 'properties' }
        (1.._) * jobManagement.requirePlugin('ant')

        when:
        context.ant('build')

        then:
        context.stepNodes.size() == 2
        def antBuildNode = context.stepNodes[1]
        antBuildNode.targets[0].value() == 'build'
        (1.._) * jobManagement.requirePlugin('ant')

        when:
        context.ant('build', 'dir1/build.xml', 'Ant 1.8')

        then:
        context.stepNodes.size() == 3
        def antArgs = context.stepNodes[2]
        antArgs.buildFile[0].value() == 'dir1/build.xml'
        antArgs.antName[0].value() == 'Ant 1.8'
        (1.._) * jobManagement.requirePlugin('ant')

        when:
        context.ant('build') {
            target 'test'
            target 'integTest'
            targets(['publish', 'deploy'])
            prop 'test.size', 4
            prop 'logging', 'info'
            props 'test.threads': 10, 'input.status': 'release'
            buildFile 'dir2/build.xml'
            buildFile 'dir1/build.xml'
            javaOpt '-Xmx1g'
            javaOpts(['-Dprop2=value2', '-Dprop3=value3'])
            antInstallation 'Ant 1.6'
            antInstallation 'Ant 1.7'
        }

        then:
        context.stepNodes.size() == 4
        def antClosure = context.stepNodes[3]
        antClosure.buildFile[0].value() == 'dir1/build.xml'
        antClosure.antName[0].value() == 'Ant 1.7'
        antClosure.targets[0].value() == 'build test integTest publish deploy'
        antClosure.antOpts[0].value() == '-Xmx1g\n-Dprop2=value2\n-Dprop3=value3'
        antClosure.'properties'[0].value() == 'test.size=4\nlogging=info\ntest.threads=10\ninput.status=release'
        (1.._) * jobManagement.requirePlugin('ant')
    }

    def 'call groovyCommand methods'() {
        when:
        context.groovyCommand("println 'Hello World!'")

        then:
        context.stepNodes.size() == 1
        def groovyNode = context.stepNodes[0]
        groovyNode.name() == 'hudson.plugins.groovy.Groovy'
        groovyNode.groovyName.size() == 1
        groovyNode.groovyName[0].value() == '(Default)'
        groovyNode.parameters.size() == 1
        groovyNode.parameters[0].value() == ''
        groovyNode.classPath.size() == 1
        groovyNode.classPath[0].value() == ''
        groovyNode.scriptParameters.size() == 1
        groovyNode.scriptParameters[0].value() == ''
        groovyNode.properties.size() == 1
        groovyNode.properties[0].value() == ''
        groovyNode.javaOpts.size() == 1
        groovyNode.javaOpts[0].value() == ''
        groovyNode.scriptSource.size() == 1
        def scriptSourceNode = groovyNode.scriptSource[0]
        scriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        scriptSourceNode.command.size() == 1
        scriptSourceNode.command[0].value() == "println 'Hello World!'"
        1 * jobManagement.requirePlugin('groovy')

        when:
        context.groovyCommand('acme.Acme.doSomething()', 'Groovy 2.0') {
            groovyParam('foo')
            groovyParams(['bar', 'baz'])
            classpath('/foo/acme.jar')
            classpath('/foo/test.jar')
            scriptParam('alfa')
            scriptParams(['bravo', 'charlie'])
            prop('one', 'two')
            props([three: 'four', five: 'six'])
            javaOpt('test')
            javaOpts(['me', 'too'])
        }

        then:
        context.stepNodes.size() == 2
        def acmeGroovyNode = context.stepNodes[1]
        acmeGroovyNode.name() == 'hudson.plugins.groovy.Groovy'
        acmeGroovyNode.groovyName.size() == 1
        acmeGroovyNode.groovyName[0].value() == 'Groovy 2.0'
        acmeGroovyNode.parameters.size() == 1
        acmeGroovyNode.parameters[0].value() == 'foo bar baz'
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == 'alfa bravo charlie'
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == 'one=two\nthree=four\nfive=six'
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == 'acme.Acme.doSomething()'
        1 * jobManagement.requirePlugin('groovy')
    }

    def 'call groovyScriptFile methods'() {
        when:
        context.groovyScriptFile('scripts/hello.groovy')

        then:
        context.stepNodes.size() == 1
        def groovyNode = context.stepNodes[0]
        groovyNode.name() == 'hudson.plugins.groovy.Groovy'
        groovyNode.groovyName.size() == 1
        groovyNode.groovyName[0].value() == '(Default)'
        groovyNode.parameters.size() == 1
        groovyNode.parameters[0].value() == ''
        groovyNode.classPath.size() == 1
        groovyNode.classPath[0].value() == ''
        groovyNode.scriptParameters.size() == 1
        groovyNode.scriptParameters[0].value() == ''
        groovyNode.properties.size() == 1
        groovyNode.properties[0].value() == ''
        groovyNode.javaOpts.size() == 1
        groovyNode.javaOpts[0].value() == ''
        groovyNode.scriptSource.size() == 1
        def scriptSourceNode = groovyNode.scriptSource[0]
        scriptSourceNode.attribute('class') == 'hudson.plugins.groovy.FileScriptSource'
        scriptSourceNode.scriptFile.size() == 1
        scriptSourceNode.scriptFile[0].value() == 'scripts/hello.groovy'
        1 * jobManagement.requirePlugin('groovy')

        when:
        context.groovyScriptFile('acme.groovy', 'Groovy 2.0') {
            groovyParam('foo')
            groovyParams(['bar', 'baz'])
            classpath('/foo/acme.jar')
            classpath('/foo/test.jar')
            scriptParam('alfa')
            scriptParams(['bravo', 'charlie'])
            prop('one', 'two')
            props([three: 'four', five: 'six'])
            javaOpt('test')
            javaOpts(['me', 'too'])
        }

        then:
        context.stepNodes.size() == 2
        def acmeGroovyNode = context.stepNodes[1]
        acmeGroovyNode.name() == 'hudson.plugins.groovy.Groovy'
        acmeGroovyNode.groovyName.size() == 1
        acmeGroovyNode.groovyName[0].value() == 'Groovy 2.0'
        acmeGroovyNode.parameters.size() == 1
        acmeGroovyNode.parameters[0].value() == 'foo bar baz'
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == 'alfa bravo charlie'
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == 'one=two\nthree=four\nfive=six'
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.FileScriptSource'
        acmeScriptSourceNode.scriptFile.size() == 1
        acmeScriptSourceNode.scriptFile[0].value() == 'acme.groovy'
        1 * jobManagement.requirePlugin('groovy')

        when:
        context.groovyScriptFile('foo.groovy') {
            groovyInstallation('Groovy 2.1')
        }

        then:
        context.stepNodes.size() == 3
        def groovy21Node = context.stepNodes[2]
        groovy21Node.groovyName.size() == 1
        groovy21Node.groovyName[0].value() == 'Groovy 2.1'
        1 * jobManagement.requirePlugin('groovy')
    }

    def 'call systemGroovyCommand methods'() {
        when:
        context.systemGroovyCommand("println 'Hello World!'")

        then:
        context.stepNodes.size() == 1
        def systemGroovyNode = context.stepNodes[0]
        systemGroovyNode.name() == 'hudson.plugins.groovy.SystemGroovy'
        systemGroovyNode.bindings.size() == 1
        systemGroovyNode.bindings[0].value() == ''
        systemGroovyNode.classpath.size() == 1
        systemGroovyNode.classpath[0].value() == ''
        systemGroovyNode.scriptSource.size() == 1
        def scriptSourceNode = systemGroovyNode.scriptSource[0]
        scriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        scriptSourceNode.command.size() == 1
        scriptSourceNode.command[0].value() == "println 'Hello World!'"
        1 * jobManagement.requirePlugin('groovy')

        when:
        context.systemGroovyCommand('acme.Acme.doSomething()') {
            binding('foo', 'bar')
            binding('test', '0815')
            classpath('/foo/acme.jar')
            classpath('/foo/test.jar')
        }

        then:
        context.stepNodes.size() == 2
        def acmeSystemGroovyNode = context.stepNodes[1]
        acmeSystemGroovyNode.name() == 'hudson.plugins.groovy.SystemGroovy'
        acmeSystemGroovyNode.bindings.size() == 1
        acmeSystemGroovyNode.bindings[0].value() == 'foo=bar\ntest=0815'
        acmeSystemGroovyNode.classpath.size() == 1
        acmeSystemGroovyNode.classpath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeSystemGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeSystemGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == 'acme.Acme.doSomething()'
        1 * jobManagement.requirePlugin('groovy')
    }

    def 'call systemGroovyScriptFile methods'() {
        when:
        context.systemGroovyScriptFile('scripts/hello.groovy')

        then:
        context.stepNodes.size() == 1
        def systemGroovyNode = context.stepNodes[0]
        systemGroovyNode.name() == 'hudson.plugins.groovy.SystemGroovy'
        systemGroovyNode.bindings.size() == 1
        systemGroovyNode.bindings[0].value() == ''
        systemGroovyNode.classpath.size() == 1
        systemGroovyNode.classpath[0].value() == ''
        systemGroovyNode.scriptSource.size() == 1
        def scriptSourceNode = systemGroovyNode.scriptSource[0]
        scriptSourceNode.attribute('class') == 'hudson.plugins.groovy.FileScriptSource'
        scriptSourceNode.scriptFile.size() == 1
        scriptSourceNode.scriptFile[0].value() == 'scripts/hello.groovy'
        1 * jobManagement.requirePlugin('groovy')

        when:
        context.systemGroovyScriptFile('acme.groovy') {
            binding('foo', 'bar')
            binding('test', '0815')
            classpath('/foo/acme.jar')
            classpath('/foo/test.jar')
        }

        then:
        context.stepNodes.size() == 2
        def acmeSystemGroovyNode = context.stepNodes[1]
        acmeSystemGroovyNode.name() == 'hudson.plugins.groovy.SystemGroovy'
        acmeSystemGroovyNode.bindings.size() == 1
        acmeSystemGroovyNode.bindings[0].value() == 'foo=bar\ntest=0815'
        acmeSystemGroovyNode.classpath.size() == 1
        acmeSystemGroovyNode.classpath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeSystemGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeSystemGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.FileScriptSource'
        acmeScriptSourceNode.scriptFile.size() == 1
        acmeScriptSourceNode.scriptFile[0].value() == 'acme.groovy'
        1 * jobManagement.requirePlugin('groovy')
    }

    def 'call copyArtifacts selector variants'() {
        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                latestSuccessful()
            }
        }

        then:
        Node selectorNode = context.stepNodes[0].selector[0]
        selectorNode.attribute('class') == 'hudson.plugins.copyartifact.StatusBuildSelector'
        selectorNode.children().size() == 0

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                latestSaved()
            }
        }

        then:
        def selectorNode2 = context.stepNodes[1].selector[0]
        selectorNode2.attribute('class') == 'hudson.plugins.copyartifact.SavedBuildSelector'
        selectorNode2.children().size() == 0

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                permalink('lastBuild')
            }
        }

        then:
        def selectorNode3 = context.stepNodes[2].selector[0]
        selectorNode3.attribute('class') == 'hudson.plugins.copyartifact.PermalinkBuildSelector'
        selectorNode3.id[0].value() == 'lastBuild'

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                buildNumber(43)
            }
        }

        then:
        def selectorNode4 = context.stepNodes[3].selector[0]
        selectorNode4.attribute('class') == 'hudson.plugins.copyartifact.SpecificBuildSelector'
        selectorNode4.buildNumber[0].value() == '43'

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                workspace()
            }
        }

        then:
        def selectorNode5 = context.stepNodes[4].selector[0]
        selectorNode5.attribute('class') == 'hudson.plugins.copyartifact.WorkspaceSelector'
        selectorNode5.children().size() == 0

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                buildParameter('BUILD_PARAM')
            }
        }

        then:
        def selectorNode6 = context.stepNodes[5].selector[0]
        selectorNode6.attribute('class') == 'hudson.plugins.copyartifact.ParameterizedBuildSelector'
        selectorNode6.parameterName[0].value() == 'BUILD_PARAM'

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                buildNumber('$SOME_PARAMTER')
            }
        }

        then:
        def selectorNode7 = context.stepNodes[6].selector[0]
        selectorNode7.attribute('class') == 'hudson.plugins.copyartifact.SpecificBuildSelector'
        selectorNode7.buildNumber[0].value() == '$SOME_PARAMTER'

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                latestSuccessful(true)
            }
        }

        then:
        Node selectorNode8 = context.stepNodes[7].selector[0]
        selectorNode8.attribute('class') == 'hudson.plugins.copyartifact.StatusBuildSelector'
        selectorNode8.children().size() == 1
        selectorNode8.stable[0].value() == true

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                multiJobBuild()
            }
        }

        then:
        Node selectorNode9 = context.stepNodes[8].selector[0]
        selectorNode9.attribute('class') == 'com.tikal.jenkins.plugins.multijob.MultiJobBuildSelector'
        selectorNode9.children().size() == 0
        1 * jobManagement.requireMinimumPluginVersion('jenkins-multijob-plugin', '1.22')
    }

    def 'copyArtifacts with selector extension'() {
        jobManagement.callExtension('foo', item, CopyArtifactSelectorContext) >> new Node(null, 'foo.Bar')

        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                foo()
            }
        }

        then:
        with(context.stepNodes[0].selector[0]) {
            attribute('class') == 'foo.Bar'
            children().size() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.31')
    }

    def 'call minimal copyArtifacts'() {
        when:
        context.copyArtifacts('upstream')

        then:
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.31')
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.copyartifact.CopyArtifact'
            children().size() == 5
            project[0].value() == 'upstream'
            filter[0].value() == ''
            target[0].value() == ''
            with(selector[0]) {
                attribute('class') == 'hudson.plugins.copyartifact.StatusBuildSelector'
                children().size() == 0
            }
            doNotFingerprintArtifacts[0].value() == false
        }
    }

    def 'call copyArtifacts all options'() {
        when:
        context.copyArtifacts('upstream') {
            includePatterns('*.xml', '*.txt')
            excludePatterns('foo.xml', 'foo.txt')
            targetDirectory('target/')
            flatten()
            optional()
            fingerprintArtifacts(false)
            parameterFilters('ONE=two', 'FOO=bar')
            parameterFilters('LOREM=ipsum')
            buildSelector {
                upstreamBuild(true)
            }
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.31')
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.copyartifact.CopyArtifact'
            children().size() == 9
            project[0].value() == 'upstream'
            filter[0].value() == '*.xml, *.txt'
            excludes[0].value() == 'foo.xml, foo.txt'
            flatten[0].value() == true
            optional[0].value() == true
            target[0].value() == 'target/'
            doNotFingerprintArtifacts[0].value() == true
            parameters[0].value() == 'ONE=two, FOO=bar, LOREM=ipsum'
            with(selector[0]) {
                children().size() == 1
                attribute('class') == 'hudson.plugins.copyartifact.TriggeredBuildSelector'
                fallbackToLastSuccessful[0].value() == true
            }
        }
    }

    def 'call copyArtifacts with upstreamBuild closure'() {
        when:
        context.copyArtifacts('upstream') {
            buildSelector {
                upstreamBuild {
                    fallbackToLastSuccessful()
                    allowUpstreamDependencies()
                }
            }
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.31')
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.37')
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.copyartifact.CopyArtifact'
            children().size() == 5
            project[0].value() == 'upstream'
            filter[0].value() == ''
            target[0].value() == ''
            doNotFingerprintArtifacts[0].value() == false
            with(selector[0]) {
                children().size() == 2
                attribute('class') == 'hudson.plugins.copyartifact.TriggeredBuildSelector'
                fallbackToLastSuccessful[0].value() == true
                allowUpstreamDependencies[0].value() == true
            }
        }
    }

    def 'call copyArtifacts all options no fingerprint'() {
        when:
        context.copyArtifacts('upstream') {
            includePatterns('*.xml', '*.txt')
            excludePatterns('foo.xml', 'foo.txt')
            targetDirectory('target/')
            flatten()
            optional()
            buildSelector {
                upstreamBuild(true)
            }
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.31')
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.copyartifact.CopyArtifact'
            children().size() == 8
            project[0].value() == 'upstream'
            filter[0].value() == '*.xml, *.txt'
            excludes[0].value() == 'foo.xml, foo.txt'
            flatten[0].value() == true
            optional[0].value() == true
            target[0].value() == 'target/'
            doNotFingerprintArtifacts[0].value() == false
            with(selector[0]) {
                children().size() == 1
                attribute('class') == 'hudson.plugins.copyartifact.TriggeredBuildSelector'
                fallbackToLastSuccessful[0].value() == true
            }
        }
    }

    def 'call resolveArtifacts with minimal arguments'() {
        when:
        context.resolveArtifacts {
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.repositoryconnector.ArtifactResolver'
            children().size() == 8
            targetDirectory[0].value() == ''
            failOnError[0].value() == false
            enableRepoLogging[0].value() == false
            snapshotUpdatePolicy[0].value() == 'daily'
            releaseUpdatePolicy[0].value() == 'daily'
            snapshotChecksumPolicy[0].value() == 'warn'
            releaseChecksumPolicy[0].value() == 'warn'
            artifacts[0].children().size() == 0
        }
        1 * jobManagement.requirePlugin('repository-connector')
    }

    def 'call resolveArtifacts with all arguments and two artifacts'() {
        when:
        context.resolveArtifacts {
            failOnError()
            enableRepoLogging()
            snapshotUpdatePolicy 'always'
            releaseUpdatePolicy 'never'
            targetDirectory 'target'
            artifact {
                groupId 'org.slf4j'
                artifactId 'slf4j-api'
                version '[1.7.5,1.7.6]'
                classifier 'javadoc'
                extension 'jar'
                targetFileName 'slf4j-api-1.7.6-TEST.jar'
            }
            artifact {
                groupId 'ch.qos.logback'
                artifactId 'logback-classic'
                version '1.1.1'
                classifier 'sources'
                extension 'jar'
                targetFileName 'logback-classic-1.1.1-TEST.jar'
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.repositoryconnector.ArtifactResolver'
            children().size() == 8
            failOnError[0].value() == true
            enableRepoLogging[0].value() == true
            snapshotUpdatePolicy[0].value() == 'always'
            releaseUpdatePolicy[0].value() == 'never'
            snapshotChecksumPolicy[0].value() == 'warn'
            releaseChecksumPolicy[0].value() == 'warn'
            artifacts[0].children().size() == 2
            with(artifacts[0].'org.jvnet.hudson.plugins.repositoryconnector.Artifact'[0]) {
                children().size() == 6
                groupId[0].value() == 'org.slf4j'
                artifactId[0].value() == 'slf4j-api'
                version[0].value() == '[1.7.5,1.7.6]'
                classifier[0].value() == 'javadoc'
                extension[0].value() == 'jar'
                targetFileName[0].value() == 'slf4j-api-1.7.6-TEST.jar'
            }
            with(artifacts[0].'org.jvnet.hudson.plugins.repositoryconnector.Artifact'[1]) {
                children().size() == 6
                groupId[0].value() == 'ch.qos.logback'
                artifactId[0].value() == 'logback-classic'
                version[0].value() == '1.1.1'
                classifier[0].value() == 'sources'
                extension[0].value() == 'jar'
                targetFileName[0].value() == 'logback-classic-1.1.1-TEST.jar'
            }
        }
        1 * jobManagement.requirePlugin('repository-connector')
    }

    def 'call sbt method minimal'() {
        when:
        context.sbt('SBT 0.12.3')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def sbtStep = context.stepNodes[0]
        sbtStep.name() == 'org.jvnet.hudson.plugins.SbtPluginBuilder'
        sbtStep.name[0].value() == 'SBT 0.12.3'
        sbtStep.jvmFlags[0].value() == ''
        sbtStep.sbtFlags[0].value() == ''
        sbtStep.actions[0].value() == ''
        sbtStep.subdirPath[0].value() == ''
        1 * jobManagement.requirePlugin('sbt')
    }

    def 'call sbt method action only'() {
        when:
        context.sbt('SBT 0.12.3', 'test')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def sbtStep = context.stepNodes[0]
        sbtStep.name() == 'org.jvnet.hudson.plugins.SbtPluginBuilder'
        sbtStep.name[0].value() == 'SBT 0.12.3'
        sbtStep.jvmFlags[0].value() == ''
        sbtStep.sbtFlags[0].value() == ''
        sbtStep.actions[0].value() == 'test'
        sbtStep.subdirPath[0].value() == ''
        1 * jobManagement.requirePlugin('sbt')
    }

    def 'call sbt method full'() {
        when:
        context.sbt(
                'SBT 0.12.3',
                'test',
                '-Dsbt.log.noformat=true',
                '-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -Dfile.encoding=UTF-8 -Xmx2G',
                'subproject'
        )

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def sbtStep = context.stepNodes[0]
        sbtStep.name() == 'org.jvnet.hudson.plugins.SbtPluginBuilder'
        sbtStep.name[0].value() == 'SBT 0.12.3'
        sbtStep.jvmFlags[0].value() == '-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -Dfile.encoding=UTF-8 -Xmx2G'
        sbtStep.sbtFlags[0].value() == '-Dsbt.log.noformat=true'
        sbtStep.actions[0].value() == 'test'
        sbtStep.subdirPath[0].value() == 'subproject'
        1 * jobManagement.requirePlugin('sbt')
    }

    def 'call dsl with defaults'() {
        when:
        context.dsl {
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
            children().size() == 8
            targets[0].value() == ''
            usingScriptText[0].value() == false
            ignoreExisting[0].value() == false
            removedJobAction[0].value() == 'IGNORE'
            removedViewAction[0].value() == 'IGNORE'
            scriptText[0].value() == ''
            additionalClasspath[0].value() == ''
            lookupStrategy[0].value() == 'JENKINS_ROOT'
        }
    }

    def 'call dsl with external scripts and all options'() {
        when:
        context.dsl {
            external('some-dsl.groovy', 'some-other-dsl.groovy')
            external('still-another-dsl.groovy')
            ignoreExisting()
            removeAction('DISABLE')
            removeViewAction('DELETE')
            additionalClasspath('some/path')
            lookupStrategy('SEED_JOB')
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
            children().size() == 8
            targets[0].value() == 'some-dsl.groovy\nsome-other-dsl.groovy\nstill-another-dsl.groovy'
            usingScriptText[0].value() == false
            ignoreExisting[0].value() == true
            removedJobAction[0].value() == 'DISABLE'
            removedViewAction[0].value() == 'DELETE'
            scriptText[0].value() == ''
            additionalClasspath[0].value() == 'some/path'
            lookupStrategy[0].value() == 'SEED_JOB'
        }
    }

    def 'call dsl with invalid lookup strategy'() {
        when:
        context.dsl {
            lookupStrategy('XXX')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call dsl with invalid remove action'() {
        when:
        context.dsl {
            removeAction('XXX')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call dsl with invalid remove view action'() {
        when:
        context.dsl {
            removeViewAction('XXX')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call dsl with script text and all options'() {
        when:
        context.dsl {
            text('''job {
  foo()
  bar {
    baz()
  }
}
''')
            ignoreExisting()
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('some/path')
            lookupStrategy('SEED_JOB')
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
            children().size() == 8
            targets[0].value() == ''
            usingScriptText[0].value() == true
            ignoreExisting[0].value() == true
            removedJobAction[0].value() == 'DELETE'
            removedViewAction[0].value() == 'DELETE'
            scriptText[0].value() == '''job {
  foo()
  bar {
    baz()
  }
}
'''
            additionalClasspath[0].value() == 'some/path'
            lookupStrategy[0].value() == 'SEED_JOB'
        }
    }

    def 'call dsl method external script as parameters'() {
        when:
        context.dsl(['some-dsl.groovy', 'some-other-dsl.groovy', 'still-another-dsl.groovy'], 'DISABLE')

        then:
        with(context.stepNodes[0]) {
            name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
            children().size() == 8
            targets[0].value() == 'some-dsl.groovy\nsome-other-dsl.groovy\nstill-another-dsl.groovy'
            usingScriptText[0].value() == false
            ignoreExisting[0].value() == false
            removedJobAction[0].value() == 'DISABLE'
            removedViewAction[0].value() == 'IGNORE'
            scriptText[0].value() == ''
            additionalClasspath[0].value() == ''
            lookupStrategy[0].value() == 'JENKINS_ROOT'
        }
    }

    def 'call dsl method external script as parameters full'() {
        when:
        context.dsl(['some-dsl.groovy', 'some-other-dsl.groovy', 'still-another-dsl.groovy'], 'DISABLE', true)

        then:
        with(context.stepNodes[0]) {
            name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
            children().size() == 8
            targets[0].value() == 'some-dsl.groovy\nsome-other-dsl.groovy\nstill-another-dsl.groovy'
            usingScriptText[0].value() == false
            ignoreExisting[0].value() == true
            removedJobAction[0].value() == 'DISABLE'
            removedViewAction[0].value() == 'IGNORE'
            scriptText[0].value() == ''
            additionalClasspath[0].value() == ''
            lookupStrategy[0].value() == 'JENKINS_ROOT'
        }
    }

    def 'call dsl method with script text as parameters'() {
        when:
        context.dsl('''job {
  foo()
  bar {
    baz()
  }
}
''', 'DELETE')

        then:
        with(context.stepNodes[0]) {
            name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
            children().size() == 8
            targets[0].value() == ''
            usingScriptText[0].value() == true
            ignoreExisting[0].value() == false
            removedJobAction[0].value() == 'DELETE'
            removedViewAction[0].value() == 'IGNORE'
            scriptText[0].value() == '''job {
  foo()
  bar {
    baz()
  }
}
'''
            additionalClasspath[0].value() == ''
            lookupStrategy[0].value() == 'JENKINS_ROOT'
        }
    }

    def 'call prerequisite method with single project'() {
        when:
        context.prerequisite('project-A')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def prerequisiteStep = context.stepNodes[0]
        prerequisiteStep.name() == 'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder'
        prerequisiteStep.projects[0].value() == 'project-A'
        prerequisiteStep.warningOnly[0].value() == false
        1 * jobManagement.requirePlugin('prereq-buildstep')
    }

    def 'call prerequisite method with multiple projects'() {
        when:
        context.prerequisite('project-A,project-B')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def prerequisiteStep = context.stepNodes[0]
        prerequisiteStep.name() == 'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder'
        prerequisiteStep.projects[0].value() == 'project-A,project-B'
        prerequisiteStep.warningOnly[0].value() == false
        1 * jobManagement.requirePlugin('prereq-buildstep')
    }

    def 'call prerequisite method with multiple projects containing leading spaces'() {
        when:
        context.prerequisite(' project-A, project-B ,project-C ')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def prerequisiteStep = context.stepNodes[0]
        prerequisiteStep.name() == 'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder'
        prerequisiteStep.projects[0].value() == 'project-A,project-B,project-C'
        prerequisiteStep.warningOnly[0].value() == false
        1 * jobManagement.requirePlugin('prereq-buildstep')
    }

    def 'call prerequisite method with single project and overriden warning only flag'() {
        when:
        context.prerequisite('project-A', true)

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def prerequisiteStep = context.stepNodes[0]
        prerequisiteStep.name() == 'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder'
        prerequisiteStep.projects[0].value() == 'project-A'
        prerequisiteStep.warningOnly[0].value() == true
        1 * jobManagement.requirePlugin('prereq-buildstep')
    }

    def 'call publishOverSsh without server'() {
        when:
        context.publishOverSsh(null)

        then:
        thrown(DslScriptException)
    }

    def 'call publishOverSsh without transferSet'() {
        when:
        context.publishOverSsh {
            server('server-name') {
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call publishOverSsh without sourceFiles and execCommand'() {
        when:
        context.publishOverSsh {
            server('server-name') {
                transferSet {
                }
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call publishOverSsh with minimal configuration and check the default values'() {
        when:
        context.publishOverSsh {
            server('server-name') {
                transferSet {
                    sourceFiles('file')
                    execCommand('command')
                }
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin'
            with(delegate.delegate[0]) {
                consolePrefix[0].value() == 'SSH: '
                with(delegate.delegate[0]) {
                    with(publishers[0]) {
                        children().size() == 1
                        with(delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[0]) {
                            configName[0].value() == 'server-name'
                            verbose[0].value() == false
                            with(transfers[0]) {
                                children().size() == 1
                                with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
                                    remoteDirectory[0].value() == ''
                                    sourceFiles[0].value() == 'file'
                                    excludes[0].value() == ''
                                    removePrefix[0].value() == ''
                                    remoteDirectorySDF[0].value() == false
                                    flatten[0].value() == false
                                    cleanRemote[0].value() == false
                                    noDefaultExcludes[0].value() == false
                                    makeEmptyDirs[0].value() == false
                                    patternSeparator[0].value() == '[, ]+'
                                    execCommand[0].value() == 'command'
                                    execTimeout[0].value() == 120000
                                    usePty[0].value() == false
                                }
                            }
                            useWorkspaceInPromotion[0].value() == false
                            usePromotionTimestamp[0].value() == false
                        }
                    }
                    continueOnError[0].value() == false
                    failOnError[0].value() == false
                    alwaysPublishFromMaster[0].value() == false
                    hostConfigurationAccess[0].@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin'
                    hostConfigurationAccess[0].@reference == '../..'
                }
            }
        }
        1 * jobManagement.requirePlugin('publish-over-ssh')
    }

    def 'call publishOverSsh with complex configuration'() {
        when:
        context.publishOverSsh {
            server('my-server-01') {
                verbose()
                credentials('user01') {
                    pathToKey('path01')
                }
                retry(10, 10000)
                label('server-01')
                transferSet {
                    sourceFiles('files')
                    execCommand('command')
                    removePrefix('prefix')
                    remoteDirectory('directory')
                    excludeFiles('exclude files')
                    patternSeparator('[| ]+')
                    noDefaultExcludes(true)
                    makeEmptyDirs()
                    flattenFiles()
                    remoteDirIsDateFormat()
                    execTimeout(11111)
                    execInPty()
                }
                transferSet {
                    sourceFiles('files2')
                    execCommand('commands2')
                }
            }
            server('my-server-02') {
                verbose(true)
                credentials('user2') {
                    key('key')
                }
                retry(20, 20000)
                label('server-02')
                transferSet {
                    sourceFiles('files3')
                    execCommand('commands3')
                }
            }
            continueOnError()
            failOnError()
            alwaysPublishFromMaster()
            parameterizedPublishing('PARAMETER')
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin'
            with(delegate.delegate[0]) {

                consolePrefix[0].value() == 'SSH: '
                with(delegate.delegate[0]) {
                    with(publishers[0]) {
                        children().size() == 2
                        with(delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[0]) {
                            configName[0].value() == 'my-server-01'
                            verbose[0].value() == true
                            with(transfers[0]) {
                                children().size() == 2
                                with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
                                    remoteDirectory[0].value() == 'directory'
                                    sourceFiles[0].value() == 'files'
                                    excludes[0].value() == 'exclude files'
                                    removePrefix[0].value() == 'prefix'
                                    remoteDirectorySDF[0].value() == true
                                    flatten[0].value() == true
                                    cleanRemote[0].value() == false
                                    noDefaultExcludes[0].value() == true
                                    makeEmptyDirs[0].value() == true
                                    patternSeparator[0].value() == '[| ]+'
                                    execCommand[0].value() == 'command'
                                    execTimeout[0].value() == 11111
                                    usePty[0].value() == true
                                }
                                with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[1]) {
                                    sourceFiles[0].value() == 'files2'
                                    execCommand[0].value() == 'commands2'
                                }
                            }
                            with(retry[0]) {
                                delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshRetry'
                                retries[0].value() == 10
                                retryDelay[0].value() == 10000
                            }
                            with(label[0]) {
                                delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherLabel'
                                label[0].value() == 'server-01'
                            }
                            with(credentials[0]) {
                                delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshCredentials'
                                secretPassphrase[0].value() == ''
                                key[0].value() == ''
                                keyPath[0].value() == 'path01'
                                username[0].value() == 'user01'
                            }
                        }
                        with(delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[1]) {
                            configName[0].value() == 'my-server-02'
                            verbose[0].value() == true
                            with(transfers[0]) {
                                children().size() == 1
                                with(delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
                                    sourceFiles[0].value() == 'files3'
                                    execCommand[0].value() == 'commands3'
                                }
                            }
                            with(retry[0]) {
                                delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshRetry'
                                retries[0].value() == 20
                                retryDelay[0].value() == 20000
                            }
                            with(label[0]) {
                                delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherLabel'
                                label[0].value() == 'server-02'
                            }
                            with(credentials[0]) {
                                delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshCredentials'
                                secretPassphrase[0].value() == ''
                                key[0].value() == 'key'
                                keyPath[0].value() == ''
                                username[0].value() == 'user2'
                            }
                        }
                    }
                    continueOnError[0].value() == true
                    failOnError[0].value() == true
                    alwaysPublishFromMaster[0].value() == true
                    hostConfigurationAccess[0].@class == 'jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin'
                    hostConfigurationAccess[0].@reference == '../..'
                    with(paramPublish[0]) {
                        delegate.@class == 'jenkins.plugins.publish_over_ssh.BapSshParamPublish'
                        parameterName[0].value() == 'PARAMETER'
                    }
                }
            }
        }
        1 * jobManagement.requirePlugin('publish-over-ssh')
    }

    def 'call downstream build step with all args'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    currentBuild() // Current build parameters
                    propertiesFile('dir/my.properties') // Parameters from properties file
                    gitRevision(false) // Pass-through Git commit that was built
                    predefinedProp('key1', 'value1') // Predefined properties
                    predefinedProps([key2: 'value2', key3: 'value3'])
                    matrixSubset('label=="${TARGET}"') // Restrict matrix execution to a subset
                    subversionRevision() // Subversion Revision
                    nodeLabel('nodeParam', 'node_label') // Limit to node label selection
                }
                parameterFactories {
                    forMatchingFiles('foo', 'bar', 'FAIL')
                }
            }
            trigger('Project2') {
                parameters {
                    currentBuild()
                }
            }
        }

        then:
        Node stepNode = context.stepNodes[0]
        stepNode.name() == 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
        stepNode.configs[0].children().size() == 2
        with(stepNode.configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
            children().size() == 6
            projects[0].value() == 'Project1, Project2'
            condition[0].value() == 'ALWAYS'
            triggerWithNoParameters[0].value() == false
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
            configs[0].'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0].propertiesFile[0].value() ==
                    'dir/my.properties'
            configs[0].'hudson.plugins.git.GitRevisionBuildParameters'[0].combineQueuedCommits[0].value() == false
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'.size() == 1
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0].'properties'[0].value() ==
                    'key1=value1\nkey2=value2\nkey3=value3'
            configs[0].'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0].filter[0].value() ==
                    'label=="${TARGET}"'
            configs[0].'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0] instanceof Node
            configs[0].'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0].
                    name[0].value() == 'nodeParam'
            configs[0].'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0].
                    nodeLabel[0].value() == 'node_label'

            block.size() == 1
            Node thresholds = block[0]
            thresholds.children().size() == 3
            Node unstableThreshold = thresholds.unstableThreshold[0]
            unstableThreshold.name[0].value() == 'UNSTABLE'
            unstableThreshold.ordinal[0].value() == 1
            unstableThreshold.completeBuild[0].value() == true
            Node failureThreshold = thresholds.failureThreshold[0]
            failureThreshold.name[0].value() == 'FAILURE'
            failureThreshold.ordinal[0].value() == 2
            failureThreshold.completeBuild[0].value() == true
            Node buildStepFailureThreshold = thresholds.buildStepFailureThreshold[0]
            buildStepFailureThreshold.name[0].value() == 'FAILURE'
            configFactories[0].children().size() == 1
            with(configFactories[0].'hudson.plugins.parameterizedtrigger.BinaryFileParameterFactory') {
                parameterName.text() == 'bar'
                filePattern.text() == 'foo'
                noFilesFoundAction.text() == 'FAIL'
            }
        }

        with(stepNode.configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[1]) {
            projects[0].value() == 'Project2'
            condition[0].value() == 'ALWAYS'
            triggerWithNoParameters[0].value() == false
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
            block.isEmpty()
        }
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
        1 * jobManagement.requirePlugin('nodelabelparameter')
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call downstream build step with no args'() {
        when:
        context.downstreamParameterized {
            trigger('Project3') {
            }
        }

        then:
        with(context.stepNodes[0].configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
            projects[0].value() == 'Project3'
            condition[0].value() == 'ALWAYS'
            triggerWithNoParameters[0].value() == false
            configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        }
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call downstream build step with project list'() {
        when:
        context.downstreamParameterized {
            trigger(['Project1', 'Project2']) {
            }
        }

        then:
        with(context.stepNodes[0].configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
            projects[0].value() == 'Project1, Project2'
            condition[0].value() == 'ALWAYS'
            triggerWithNoParameters[0].value() == false
            configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        }
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call downstream build step with no args and older plugin version'() {
        when:
        context.downstreamParameterized {
            trigger('Project3') {
            }
        }

        then:
        with(context.stepNodes[0].configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
            children().size() == 4
            projects[0].value() == 'Project3'
            condition[0].value() == 'ALWAYS'
            triggerWithNoParameters[0].value() == false
            configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        }
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call downstream build step with default blocking options'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                }
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
            children().size() == 1
            configs[0].children().size() == 1
            with(configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
                children().size() == 5
                projects[0].value() == 'Project1, Project2'
                condition[0].value() == 'ALWAYS'
                triggerWithNoParameters[0].value() == false
                configs[0].@class == 'java.util.Collections$EmptyList'
                configs[0].children().size() == 0
                block[0].children().size() == 0
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call downstream build step with blocking options'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    buildStepFailure(threshold)
                    failure(threshold)
                    unstable(threshold)
                }
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
            children().size() == 1
            configs[0].children().size() == 1
            with(configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
                children().size() == 5
                projects[0].value() == 'Project1, Project2'
                condition[0].value() == 'ALWAYS'
                triggerWithNoParameters[0].value() == false
                configs[0].@class == 'java.util.Collections$EmptyList'
                configs[0].children().size() == 0
                with(block[0]) {
                    if (threshold == 'never') {
                        children().size() == 0
                    } else {
                        children().size() == 3
                        with(buildStepFailureThreshold[0]) {
                            children().size() == 4
                            name[0].value() == threshold
                            ordinal[0].value() == ordinalValue
                            color[0].value() == colorValue
                            completeBuild[0].value() == true
                        }
                        with(failureThreshold[0]) {
                            children().size() == 4
                            name[0].value() == threshold
                            ordinal[0].value() == ordinalValue
                            color[0].value() == colorValue
                            completeBuild[0].value() == true
                        }
                        with(unstableThreshold[0]) {
                            children().size() == 4
                            name[0].value() == threshold
                            ordinal[0].value() == ordinalValue
                            color[0].value() == colorValue
                            completeBuild[0].value() == true
                        }
                    }
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')

        where:
        threshold  || ordinalValue | colorValue
        'never'    || null | null
        'SUCCESS'  || 0 | 'BLUE'
        'UNSTABLE' || 1 | 'YELLOW'
        'FAILURE'  || 2 | 'RED'
    }

    def 'call downstream build step with invalid blocking options'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    buildStepFailure(threshold)
                }
            }
        }

        then:
        thrown(DslScriptException)

        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    failure(threshold)
                }
            }
        }

        then:
        thrown(DslScriptException)

        when:
        context.downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    unstable(threshold)
                }
            }
        }

        then:
        thrown(DslScriptException)

        where:
        threshold << [null, '', 'FOO']
    }

    @Unroll
    def 'call conditional steps for a single step with #testCondition'() {
        when:
        context.conditionalSteps {
            condition {
                delegate.invokeMethod(testCondition, testConditionArgs.values().toArray())
            }
            runner('Fail')
            steps {
                shell('look at me')
            }
        }

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'
        step.runCondition[0].children().size() == testConditionArgs.values().size()

        Node condition = step.runCondition[0]
        condition.attribute('class') == "org.jenkins_ci.plugins.run_condition.core.${testConditionClass}"
        if (!testConditionArgs.isEmpty()) {
            testConditionArgs.each { k, v ->
                condition[k][0].value() == v
            }
        }
        step.runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'

        step.conditionalbuilders[0].children().size() == 1
        Node childStep = step.conditionalbuilders[0].children()[0]
        childStep.name() == 'hudson.tasks.Shell'
        childStep.command[0].value() == 'look at me'

        1 * jobManagement.requirePlugin('conditional-buildstep')

        where:
        testCondition << [
                'stringsMatch', 'alwaysRun', 'neverRun', 'booleanCondition', 'cause', 'expression', 'time'
        ]
        testConditionArgs << [
                ['arg1': 'foo', 'arg2': 'bar', 'ignoreCase': false], [:], [:],
                ['token': 'foo'], ['buildCause': 'foo', 'exclusiveCause': true],
                ['expression': 'some-expression', 'label': 'some-label'],
                ['earliestHours': 9, 'earliestMinutes': 10,
                 'latestHours': 14, 'latestMinutes': 45,
                 'useBuildTime': false]
        ]
        testConditionClass << [
                'StringsMatchCondition', 'AlwaysRun', 'NeverRun', 'BooleanCondition', 'CauseCondition',
                'ExpressionCondition', 'TimeCondition'
        ]
    }

    def 'call conditional steps with nodes condition'() {
        when:
        context.conditionalSteps {
            condition {
                nodes(['foo', 'bar'])
            }
            steps {
                shell('look at me')
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'
            children().size() == 3
            with(runCondition[0]) {
                attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.NodeCondition'
                children().size() == 1
                allowedNodes[0].children().size() == 2
                allowedNodes[0].string[0].value() == 'foo'
                allowedNodes[0].string[1].value() == 'bar'
            }
            runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
            conditionalbuilders[0].children().size() == 1
            with(conditionalbuilders[0].children()[0]) {
                name() == 'hudson.tasks.Shell'
                children().size() == 1
                command[0].value() == 'look at me'
            }
        }
        1 * jobManagement.requirePlugin('conditional-buildstep')
        1 * jobManagement.requireMinimumPluginVersion('run-condition', '1.0')
    }

    @Unroll
    def 'call conditional steps for a single step with #runner'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            runner(runnerName)
            steps {
                shell('look at me')
            }
        }

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'

        step.runner[0].attribute('class') == "org.jenkins_ci.plugins.run_condition.BuildStepRunner\$${runnerName}"

        step.conditionalbuilders[0].children().size() == 1
        Node childStep = step.conditionalbuilders[0].children()[0]
        childStep.name() == 'hudson.tasks.Shell'
        childStep.command[0].value() == 'look at me'

        1 * jobManagement.requirePlugin('conditional-buildstep')

        where:
        runnerName << ['Fail', 'Unstable', 'RunUnstable', 'Run', 'DontRun']
    }

    def 'call conditional steps without runner'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            steps {
                shell('look at me')
            }
        }

        then:
        with(context.stepNodes[0]) {
            children().size() == 3
            name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'
            runCondition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
            runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
            conditionalbuilders[0].children().size() == 1
        }
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }

    def 'call conditional steps with unknown runner'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            runner('invalid-runner')
            steps {
                shell('look at me')
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call conditional steps with no condition'() {
        when:
        context.conditionalSteps {
            condition {
            }
            runner('Fail')
            steps {
                shell('look at me')
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call conditional steps with invalid condition'() {
        when:
        context.conditionalSteps {
            condition {
                invalidCondition()
            }
            runner('Fail')
            steps {
                shell('look at me')
            }
        }

        then:
        thrown(MissingMethodException)
    }

    def 'call conditional steps for not condition'() {
        when:
        context.conditionalSteps {
            condition {
                not {
                    stringsMatch('foo', 'bar', false)
                }
            }
            steps {
                shell('echo Test')
            }
        }

        then:
        Node step = context.stepNodes[0]
        step.runCondition[0].children().size() == 1

        Node notCondition = step.runCondition[0]
        notCondition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.logic.Not'
        Node matchCondition = notCondition.condition[0]
        matchCondition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
        matchCondition.arg1[0].value() == 'foo'
        matchCondition.arg2[0].value() == 'bar'
        matchCondition.ignoreCase[0].value() == false
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }

    def 'call conditional steps for multiple steps'() {
        when:
        context.conditionalSteps {
            condition {
                stringsMatch('foo', 'bar', false)
            }
            runner('Fail')
            steps {
                shell('look at me')
                groovyCommand('acme.Acme.doSomething()', 'Groovy 2.0') {
                    groovyParam('foo')
                    groovyParams(['bar', 'baz'])
                    classpath('/foo/acme.jar')
                    classpath('/foo/test.jar')
                    scriptParam('alfa')
                    scriptParams(['bravo', 'charlie'])
                    prop('one', 'two')
                    props([three: 'four', five: 'six'])
                    javaOpt('test')
                    javaOpts(['me', 'too'])
                }
            }
        }

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'
        step.runCondition[0].children().size() == 3

        Node condition = step.runCondition[0]
        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
        condition.arg1[0].value() == 'foo'
        condition.arg2[0].value() == 'bar'
        condition.ignoreCase[0].value() == false

        step.runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'

        step.conditionalbuilders[0].children().size() == 2

        Node shellStep = step.conditionalbuilders[0].children()[0]
        shellStep.name() == 'hudson.tasks.Shell'
        shellStep.command[0].value() == 'look at me'

        def acmeGroovyNode = step.conditionalbuilders[0].children()[1]
        acmeGroovyNode.name() == 'hudson.plugins.groovy.Groovy'
        acmeGroovyNode.groovyName.size() == 1
        acmeGroovyNode.groovyName[0].value() == 'Groovy 2.0'
        acmeGroovyNode.parameters.size() == 1
        acmeGroovyNode.parameters[0].value() == 'foo bar baz'
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == 'alfa bravo charlie'
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == 'one=two\nthree=four\nfive=six'
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == 'acme.Acme.doSomething()'
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }

    def 'fileExists is added correctly'() {
        when:
        context.conditionalSteps {
            condition {
                fileExists('someFile', RunConditionContext.BaseDir.WORKSPACE)
            }
            steps {
                shell('echo Test')
            }
        }

        then:
        Node step = context.stepNodes[0]
        step.runCondition[0].children().size() == 2

        Node condition = step.runCondition[0]
        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.FileExistsCondition'
        condition.file[0].value() == 'someFile'
        condition.baseDir[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.common.BaseDirectory$Workspace'
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }

    def 'fileMatch is added correctly'() {
        when:
        context.conditionalSteps {
            condition {
                filesMatch('someFile')
            }
            steps {
                shell('echo Test')
            }
        }

        then:
        Node step = context.stepNodes[0]
        step.runCondition[0].children().size() == 3

        Node condition = step.runCondition[0]
        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.FilesMatchCondition'
        condition.includes[0].value() == 'someFile'
        condition.excludes[0].value() == ''
        condition.baseDir[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.common.BaseDirectory$Workspace'
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }

    def 'status condition is added correctly'() {
        when:
        context.conditionalSteps {
            condition {
                status 'FAILURE', 'SUCCESS'
            }
            steps {
                shell 'echo Test'
            }
        }

        then:
        Node step = context.stepNodes[0]
        Node condition = step.runCondition[0]

        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StatusCondition'
        condition.children().size() == 2

        condition.worstResult[0].children().size() == 1
        condition.worstResult[0].ordinal[0].value() == 2
        condition.bestResult[0].children().size() == 1
        condition.bestResult[0].ordinal[0].value() == 0
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }

    @Unroll
    def 'Logical Operation #dslOperation is added correctly'(String dslOperation, String operation) {
        when:
        context.conditionalSteps {
            condition {
                "${dslOperation}" {
                    fileExists('someFile', RunConditionContext.BaseDir.WORKSPACE)
                } {
                    alwaysRun()
                }
            }
            steps {
                shell('echo Test')
            }
        }

        then:
        Node step = context.stepNodes[0]

        def logicOperation = step.runCondition[0]
        logicOperation.attribute('class') == operation
        logicOperation.children().size() == 1

        Node conditions = logicOperation.conditions[0]
        conditions.children().size() == 2

        def containers = conditions.'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer'
        with(containers[0].condition[0]) {
            attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.FileExistsCondition'
            file[0].value() == 'someFile'
            baseDir[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.common.BaseDirectory$Workspace'
        }
        containers[1].condition[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
        1 * jobManagement.requirePlugin('conditional-buildstep')

        where:
        dslOperation | operation
        'and'        | 'org.jenkins_ci.plugins.run_condition.logic.And'
        'or'         | 'org.jenkins_ci.plugins.run_condition.logic.Or'
    }

    @Unroll
    def 'Simple Condition #conditionDsl is added correctly'(conditionDsl, args, conditionClass, argNodes) {
        when:
        context.conditionalSteps {
            condition {
                "${conditionDsl}"(*args)
            }
            steps {
                shell('echo something outside')
            }
        }

        then:
        Node step = context.stepNodes[0]
        Node conditionNode = step.runCondition[0]
        conditionNode.children().size() == argNodes.size()

        conditionNode.attribute('class') == "org.jenkins_ci.plugins.run_condition.${conditionClass}"
        def ignored = argNodes.each { name, value ->
            assert conditionNode[name][0].value() == value
        }
        1 * jobManagement.requirePlugin('conditional-buildstep')

        where:
        conditionDsl       | args                     | conditionClass                   | argNodes
        'shell'            | ['echo test']            | 'contributed.ShellCondition'     | [command: 'echo test']
        'batch'            | ['xcopy * ..\\']         | 'contributed.BatchFileCondition' | [command: 'xcopy * ..\\']
        'alwaysRun'        | []                       | 'core.AlwaysRun'                 | [:]
        'neverRun'         | []                       | 'core.NeverRun'                  | [:]
        'booleanCondition' | ['someToken']            | 'core.BooleanCondition'          | [token: 'someToken']
        'cause'            | ['userCause', true]      | 'core.CauseCondition'            | [buildCause    : 'userCause',
                                                                                            exclusiveCause: true]
        'stringsMatch'     | ['some1', 'some2', true] | 'core.StringsMatchCondition'     | [arg1      : 'some1',
                                                                                            arg2      : 'some2',
                                                                                            ignoreCase: true]
        'expression'       | ['exp', 'lab']           | 'core.ExpressionCondition'       | [expression: 'exp',
                                                                                            label     : 'lab']
        'time'             | [5, 30, 15, 25, true]    | 'core.TimeCondition'             | [earliestHours  : 5,
                                                                                            earliestMinutes: 30,
                                                                                            latestHours    : 15,
                                                                                            latestMinutes  : 25,
                                                                                            useBuildTime   : true]
    }

    @Unroll
    def 'Status Condition with invalid arguments'(String worstResult, String bestResult) {
        when:
        context.conditionalSteps {
            condition {
                status(worstResult, bestResult)
            }
            steps {
                shell('echo something outside')
            }
        }

        then:
        thrown(DslScriptException)

        where:
        worstResult | bestResult
        'FOO'       | 'SUCCESS'
        'FAILURE'   | 'BAR'
        'SUCCESS'   | 'ABORTED'
    }

    @Unroll
    def 'Method #method should work within Category'(method, parameters) {
        when:
        use(ArbitraryCategory) {
            context."$method"(parameters)
        }
        then:
        notThrown(MissingMethodException)

        where:
        method               || parameters
        'groovyCommand'       | ['println "Test"']
        'systemGroovyCommand' | ['println "Test"']
        'dsl'                 | 'job { name "test" }'
    }

    def 'environmentVariables are added'() {
        when:
        context.environmentVariables {
            propertiesFile 'some.properties'
            envs test: 'some', other: 'any'
            env 'some', 'value'
        }
        Node envNode = context.stepNodes[0]

        then:
        envNode.name() == 'EnvInjectBuilder'
        envNode.info[0].children().size() == 2
        envNode.info[0].propertiesFilePath[0].value() == 'some.properties'
        envNode.info[0].propertiesContent[0].value() == 'test=some\nother=any\nsome=value'
        1 * jobManagement.requirePlugin('envinject')
    }

    def 'call remoteTrigger with minimal options'() {
        when:
        context.remoteTrigger('dev-ci', 'test')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration'
            children().size() == 14
            token[0].value() == ''
            remoteJenkinsName[0].value() == 'dev-ci'
            job[0].value() == 'test'
            shouldNotFailBuild[0].value() == false
            pollInterval[0].value() == 10
            preventRemoteBuildQueue[0].value() == false
            blockBuildUntilComplete[0].value() == false
            parameters[0].value() == ''
            parameterList[0].children().size() == 1
            parameterList[0].string[0].value() == []
            overrideAuth[0].value() == false
            auth[0].children().size() == 1
            with(auth[0].'org.jenkinsci.plugins.ParameterizedRemoteTrigger.Auth'[0]) {
                children().size() == 3
                NONE[0].value() == 'none'
                API__TOKEN[0].value() == 'apiToken'
                CREDENTIALS__PLUGIN[0].value() == 'credentialsPlugin'
            }
            loadParamsFromFile[0].value() == false
            parameterFile[0].value() == []
            queryString[0].value() == []
        }
        1 * jobManagement.requirePlugin('Parameterized-Remote-Trigger')
        1 * jobManagement.logPluginDeprecationWarning('Parameterized-Remote-Trigger', '2.0')
    }

    def 'call remoteTrigger with parameters and credentials'() {
        when:
        context.remoteTrigger('dev-ci', 'test') {
            parameter 'foo', '1'
            parameters bar: '2', baz: '3'
            overrideCredentials('test')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration'
            children().size() == 14
            token[0].value() == ''
            remoteJenkinsName[0].value() == 'dev-ci'
            job[0].value() == 'test'
            shouldNotFailBuild[0].value() == false
            pollInterval[0].value() == 10
            preventRemoteBuildQueue[0].value() == false
            blockBuildUntilComplete[0].value() == false
            parameters[0].value() == 'foo=1\nbar=2\nbaz=3'
            parameterList[0].children().size() == 3
            parameterList[0].string[0].value() == 'foo=1'
            parameterList[0].string[1].value() == 'bar=2'
            parameterList[0].string[2].value() == 'baz=3'
            overrideAuth[0].value() == true
            auth[0].children().size() == 1
            with(auth[0].'org.jenkinsci.plugins.ParameterizedRemoteTrigger.Auth'[0]) {
                children().size() == 5
                authType[0].value() == 'credentialsPlugin'
                creds[0].value() == 'test'
                NONE[0].value() == 'none'
                API__TOKEN[0].value() == 'apiToken'
                CREDENTIALS__PLUGIN[0].value() == 'credentialsPlugin'
            }
            loadParamsFromFile[0].value() == false
            parameterFile[0].value() == []
            queryString[0].value() == []
        }
        1 * jobManagement.requirePlugin('Parameterized-Remote-Trigger')
        1 * jobManagement.requireMinimumPluginVersion('Parameterized-Remote-Trigger', '2.0')
        1 * jobManagement.logPluginDeprecationWarning('Parameterized-Remote-Trigger', '2.0')
    }

    def 'call remoteTrigger with parameters and config'() {
        when:
        context.remoteTrigger('dev-ci', 'test') {
            parameter 'foo', '1'
            parameters bar: '2', baz: '3'
            shouldNotFailBuild true
            pollInterval 100
            preventRemoteBuildQueue true
            blockBuildUntilComplete true
            token 'test'
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration'
            children().size() == 14
            token[0].value() == 'test'
            remoteJenkinsName[0].value() == 'dev-ci'
            job[0].value() == 'test'
            shouldNotFailBuild[0].value() == true
            pollInterval[0].value() == 100
            preventRemoteBuildQueue[0].value() == true
            blockBuildUntilComplete[0].value() == true
            parameters[0].value() == 'foo=1\nbar=2\nbaz=3'
            parameterList[0].children().size() == 3
            parameterList[0].string[0].value() == 'foo=1'
            parameterList[0].string[1].value() == 'bar=2'
            parameterList[0].string[2].value() == 'baz=3'
            overrideAuth[0].value() == false
            auth[0].children().size() == 1
            with(auth[0].'org.jenkinsci.plugins.ParameterizedRemoteTrigger.Auth'[0]) {
                children().size() == 3
                NONE[0].value() == 'none'
                API__TOKEN[0].value() == 'apiToken'
                CREDENTIALS__PLUGIN[0].value() == 'credentialsPlugin'
            }
            loadParamsFromFile[0].value() == false
            parameterFile[0].value() == []
            queryString[0].value() == []
        }
        1 * jobManagement.requirePlugin('Parameterized-Remote-Trigger')
        1 * jobManagement.logPluginDeprecationWarning('Parameterized-Remote-Trigger', '2.0')
    }

    def 'call remoteTrigger without jenkins'() {
        when:
        context.remoteTrigger(null, 'test')

        then:
        thrown(DslScriptException)

        when:
        context.remoteTrigger('', 'test')

        then:
        thrown(DslScriptException)
    }

    def 'call remoteTrigger without job'() {
        when:
        context.remoteTrigger('dev-ci', null)

        then:
        thrown(DslScriptException)

        when:
        context.remoteTrigger('dev-ci', '')

        then:
        thrown(DslScriptException)
    }

    def 'critical block'() {
        when:
        context.criticalBlock {
            shell('echo foo')
        }

        then:
        context.stepNodes.size() == 3
        context.stepNodes[0].name() == 'org.jvnet.hudson.plugins.exclusion.CriticalBlockStart'
        context.stepNodes[1].name() == 'hudson.tasks.Shell'
        context.stepNodes[2].name() == 'org.jvnet.hudson.plugins.exclusion.CriticalBlockEnd'
        1 * jobManagement.requireMinimumPluginVersion('Exclusion', '0.12')
    }

    def 'call rake method'() {
        when:
        context.rake()

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def rakeStep = context.stepNodes[0]
        rakeStep.name() == 'hudson.plugins.rake.Rake'
        rakeStep.children().size() == 7
        rakeStep.rakeInstallation[0].value() == '(Default)'
        rakeStep.rakeFile[0].value() == ''
        rakeStep.rakeLibDir[0].value() == ''
        rakeStep.rakeWorkingDir[0].value() == ''
        rakeStep.tasks[0].value() == ''
        rakeStep.silent[0].value() == false
        rakeStep.bundleExec[0].value() == false
        (1.._) * jobManagement.requirePlugin('rake')
    }

    def 'call rake method with tasks as argument'() {
        when:
        context.rake('test') {
            file '/tmp/Rakefile'
            installation 'ruby-2.0.0-p481'
            libDir './rakelib'
            workingDir '/opt/application'
            bundleExec true
            silent true
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def rakeStep = context.stepNodes[0]
        rakeStep.name() == 'hudson.plugins.rake.Rake'
        rakeStep.children().size() == 7
        rakeStep.rakeInstallation[0].value() == 'ruby-2.0.0-p481'
        rakeStep.rakeFile[0].value() == '/tmp/Rakefile'
        rakeStep.rakeLibDir[0].value() == './rakelib'
        rakeStep.rakeWorkingDir[0].value() == '/opt/application'
        rakeStep.tasks[0].value() == 'test'
        rakeStep.silent[0].value() == true
        rakeStep.bundleExec[0].value() == true
        1 * jobManagement.requirePlugin('rake')
    }

    def 'call rake method with tasks in closure'() {
        when:
        context.rake {
            task('first')
            task('second')
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def rakeStep = context.stepNodes[0]
        rakeStep.name() == 'hudson.plugins.rake.Rake'
        rakeStep.children().size() == 7
        rakeStep.rakeInstallation[0].value() == '(Default)'
        rakeStep.rakeFile[0].value() == ''
        rakeStep.rakeLibDir[0].value() == ''
        rakeStep.rakeWorkingDir[0].value() == ''
        rakeStep.tasks[0].value() == 'first second'
        rakeStep.silent[0].value() == false
        rakeStep.bundleExec[0].value() == false
        (1.._) * jobManagement.requirePlugin('rake')
    }

    def 'call rake method with task as argument and tasks in closure'() {
        when:
        context.rake('first') {
            task('second')
            task('third')
            tasks(['fourth', 'fifth'])
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def rakeStep = context.stepNodes[0]
        rakeStep.name() == 'hudson.plugins.rake.Rake'
        rakeStep.children().size() == 7
        rakeStep.rakeInstallation[0].value() == '(Default)'
        rakeStep.rakeFile[0].value() == ''
        rakeStep.rakeLibDir[0].value() == ''
        rakeStep.rakeWorkingDir[0].value() == ''
        rakeStep.tasks[0].value() == 'first second third fourth fifth'
        rakeStep.silent[0].value() == false
        rakeStep.bundleExec[0].value() == false
        1 * jobManagement.requirePlugin('rake')
    }

    def 'call rake method with default arguments in closure'() {
        when:
        context.rake {
            task('first')
            silent()
            bundleExec()
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def rakeStep = context.stepNodes[0]
        rakeStep.name() == 'hudson.plugins.rake.Rake'
        rakeStep.children().size() == 7
        rakeStep.rakeInstallation[0].value() == '(Default)'
        rakeStep.rakeFile[0].value() == ''
        rakeStep.rakeLibDir[0].value() == ''
        rakeStep.rakeWorkingDir[0].value() == ''
        rakeStep.tasks[0].value() == 'first'
        rakeStep.silent[0].value() == true
        rakeStep.bundleExec[0].value() == true
        (1.._) * jobManagement.requirePlugin('rake')
    }

    def 'call setBuildResult'() {
        when:
        context.setBuildResult(result)

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jenkins__ci.plugins.fail__the__build.FixResultBuilder'
            children().size == 1
            defaultResultName[0].value() == result
        }
        1 * jobManagement.requireMinimumPluginVersion('fail-the-build-plugin', '1.0')

        where:
        result << ['SUCCESS', 'UNSTABLE', 'FAILURE', 'ABORTED', 'CYCLE']
    }

    def 'call setBuildResult with invalid result'() {
        when:
        context.setBuildResult('FOO')

        then:
        thrown(DslScriptException)
    }

    def 'vSphere power off'() {
        setup:
        jobManagement.getVSphereCloudHash('vsphere.acme.org') >> 4711

        when:
        context.vSpherePowerOff('vsphere.acme.org', 'foo')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer'
            children().size() == 3
            buildStep[0].attribute('class') == 'org.jenkinsci.plugins.vsphere.builders.PowerOff'
            buildStep[0].children().size() == 3
            buildStep[0].vm[0].value() == 'foo'
            buildStep[0].evenIfSuspended[0].value() == false
            buildStep[0].shutdownGracefully[0].value() == false
            serverName[0].value() == 'vsphere.acme.org'
            serverHash[0].value() == 4711
        }
        1 * jobManagement.requirePlugin('vsphere-cloud')
    }

    def 'vSphere power on'() {
        setup:
        jobManagement.getVSphereCloudHash('vsphere.acme.org') >> 4711

        when:
        context.vSpherePowerOn('vsphere.acme.org', 'foo')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer'
            children().size() == 3
            buildStep[0].attribute('class') == 'org.jenkinsci.plugins.vsphere.builders.PowerOn'
            buildStep[0].children().size() == 2
            buildStep[0].vm[0].value() == 'foo'
            buildStep[0].timeoutInSeconds[0].value() == 180
            serverName[0].value() == 'vsphere.acme.org'
            serverHash[0].value() == 4711
        }
        (1.._) * jobManagement.requirePlugin('vsphere-cloud')
    }

    def 'vSphere power on with timeout'() {
        setup:
        jobManagement.getVSphereCloudHash('vsphere.acme.org') >> 4711

        when:
        context.vSpherePowerOn('vsphere.acme.org', 'foo', 300)

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer'
            children().size() == 3
            buildStep[0].attribute('class') == 'org.jenkinsci.plugins.vsphere.builders.PowerOn'
            buildStep[0].children().size() == 2
            buildStep[0].vm[0].value() == 'foo'
            buildStep[0].timeoutInSeconds[0].value() == 300
            serverName[0].value() == 'vsphere.acme.org'
            serverHash[0].value() == 4711
        }
        1 * jobManagement.requirePlugin('vsphere-cloud')
    }

    def 'vSphere revert to snapshot'() {
        setup:
        jobManagement.getVSphereCloudHash('vsphere.acme.org') >> 4711

        when:
        context.vSphereRevertToSnapshot('vsphere.acme.org', 'foo', 'clean')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer'
            children().size() == 3
            buildStep[0].attribute('class') == 'org.jenkinsci.plugins.vsphere.builders.RevertToSnapshot'
            buildStep[0].children().size() == 2
            buildStep[0].vm[0].value() == 'foo'
            buildStep[0].snapshotName[0].value() == 'clean'
            serverName[0].value() == 'vsphere.acme.org'
            serverHash[0].value() == 4711
        }
        1 * jobManagement.requirePlugin('vsphere-cloud')
    }

    def 'vSphere deploy from template'() {
        setup:
        jobManagement.getVSphereCloudHash('vsphere.acme.org') >> 4711

        when:
        context.vSphereDeployFromTemplate {
            server('vsphere.acme.org')
            template('template')
            clone('clone')
            cluster('cluster')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer'
            children().size() == 3
            with(buildStep[0]) {
                attribute('class') == 'org.jenkinsci.plugins.vsphere.builders.Deploy'
                children().size() == 6
                template[0].value() == 'template'
                clone[0].value() == 'clone'
                cluster[0].value() == 'cluster'
                datastore[0].value().empty
                resourcePool[0].value().empty
                linkedClone[0].value() == false
            }
            serverName[0].value() == 'vsphere.acme.org'
            serverHash[0].value() == 4711
        }
        1 * jobManagement.requireMinimumPluginVersion('vsphere-cloud', '2.7')
    }

    def 'vSphere deploy from template without template'() {
        when:
        context.vSphereDeployFromTemplate {
            server('vsphere.acme.org')
            clone('clone')
            cluster('cluster')
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'template must be specified'
    }

    def 'vSphere deploy from template without clone'() {
        when:
        context.vSphereDeployFromTemplate {
            server('vsphere.acme.org')
            template('template')
            cluster('cluster')
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'clone must be specified'
    }

    def 'vSphere deploy from template without cluster'() {
        when:
        context.vSphereDeployFromTemplate {
            server('vsphere.acme.org')
            template('template')
            clone('clone')
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'cluster must be specified'
    }

    def 'vSphere server not found'() {
        when:
        context.vSpherePowerOff('vsphere.acme.org', 'foo')

        then:
        thrown(DslScriptException)
    }

    def 'call http request with minimal options'() {
        when:
        context.httpRequest('http://www.example.com')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.http__request.HttpRequest'
            children().size() == 1
            url[0].value() == 'http://www.example.com'
        }
        1 * jobManagement.requirePlugin('http_request')
    }

    def 'call http request with all options'() {
        when:
        context.httpRequest('http://www.example.com') {
            httpMode('GET')
            authentication('bob')
            returnCodeBuildRelevant()
            logResponseBody()
            passBuildParameters()
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.http__request.HttpRequest'
            children().size() == 6
            url[0].value() == 'http://www.example.com'
            httpMode[0].value() == 'GET'
            authentication[0].value() == 'bob'
            returnCodeBuildRelevant[0].value() == true
            logResponseBody[0].value() == true
            passBuildParameters[0].value() == true
        }
        1 * jobManagement.requirePlugin('http_request')
        1 * jobManagement.requireMinimumPluginVersion('http_request', '1.8.7')
    }

    def 'call http request with invalid HTTP mode'() {
        when:
        context.httpRequest('http://www.example.com') {
            httpMode('WHAT')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call http request with valid HTTP mode'(String mode) {
        when:
        context.httpRequest('http://www.example.com') {
            httpMode(mode)
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.http__request.HttpRequest'
            children().size() == 2
            url[0].value() == 'http://www.example.com'
            httpMode[0].value() == mode
        }
        1 * jobManagement.requirePlugin('http_request')

        where:
        mode << ['GET', 'POST', 'PUT', 'DELETE']
    }

    def 'call clangScanBuild with minimum options'() {
        when:
        context.clangScanBuild {
            workspace('foo')
            scheme('bar')
            clangInstallationName('test')
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.clangscanbuild.ClangScanBuildBuilder'
            children().size() == 7
            targetSdk[0].value() == 'iphonesimulator'
            config[0].value() == 'Debug'
            clangInstallationName[0].value() == 'test'
            workspace[0].value() == 'foo'
            scheme[0].value() == 'bar'
            scanbuildargs[0].value() == '--use-analyzer Xcode'
            xcodebuildargs[0].value() == '-derivedDataPath $WORKSPACE/build'
        }
        1 * jobManagement.requireMinimumPluginVersion('clang-scanbuild-plugin', '1.6')
    }

    def 'call clangScanBuild with all options'() {
        when:
        context.clangScanBuild {
            targetSdk('1')
            configuration('2')
            clangInstallationName('3')
            workspace('4')
            scheme('5')
            scanBuildArgs('6')
            xcodeBuildArgs('7')
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.clangscanbuild.ClangScanBuildBuilder'
            children().size() == 7
            targetSdk[0].value() == '1'
            config[0].value() == '2'
            clangInstallationName[0].value() == '3'
            workspace[0].value() == '4'
            scheme[0].value() == '5'
            scanbuildargs[0].value() == '6'
            xcodebuildargs[0].value() == '7'
        }
        1 * jobManagement.requireMinimumPluginVersion('clang-scanbuild-plugin', '1.6')
    }

    def 'call clangScanBuild with missing options'() {
        when:
        context.clangScanBuild {
            workspace(workspaceOption)
            scheme(schemeOption)
            clangInstallationName(clangInstallationNameOption)
        }

        then:
        thrown(DslScriptException)

        where:
        workspaceOption | schemeOption | clangInstallationNameOption
        'foo'           | 'bar'        | ''
        'foo'           | 'bar'        | null
        'foo'           | ''           | 'test'
        'foo'           | null         | 'test'
        ''              | 'bar'        | 'test'
        null            | 'bar'        | 'test'
    }

    def 'call nodejsCommand method'() {
        when:
        context.nodejsCommand('var test = require("node");', 'node (0.0.1)')

        then:
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.nodejs.NodeJsCommandInterpreter'
            children().size() == 2
            command[0].value() == 'var test = require("node");'
            nodeJSInstallationName[0].value() == 'node (0.0.1)'
        }
        1 * jobManagement.requirePlugin('nodejs')
    }

    def 'call debian package without required option'() {
        when:
        context.debianPackage ''

        then:
        thrown(DslScriptException)
    }

    def 'call debian package with only required option'() {
        when:
        context.debianPackage('package')

        then:
        with(context.stepNodes[0]) {
            name() == 'ru.yandex.jenkins.plugins.debuilder.DebianPackageBuilder'
            children().size() == 5
            pathToDebian[0].value() == 'package'
            nextVersion[0].value() == ''
            generateChangelog[0].value() == false
            signPackage[0].value() == true
            buildEvenWhenThereAreNoChanges[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('debian-package-builder', '1.6.6')
    }

    def 'call debian package with all options'() {
        when:
        context.debianPackage('package') {
            signPackage(false)
            generateChangelog('1.0', true)
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'ru.yandex.jenkins.plugins.debuilder.DebianPackageBuilder'
            children().size() == 5
            pathToDebian[0].value() == 'package'
            nextVersion[0].value() == '1.0'
            generateChangelog[0].value() == true
            signPackage[0].value() == false
            buildEvenWhenThereAreNoChanges[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('debian-package-builder', '1.6.6')
    }

    def 'call python'() {
        when:
        context.python {
            command('python setup.py')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.shiningpanda.builders.PythonBuilder'
            children().size() == 4
            command[0].value() == 'python setup.py'
            ignoreExitCode[0].value() == false
            nature[0].value() == 'shell'
            pythonName[0].value() == 'System-CPython-2.7'
        }
        1 * jobManagement.requireMinimumPluginVersion('shiningpanda', '0.21')
    }

    def 'call python with all options'() {
        when:
        context.python {
            command('python setup.py')
            ignoreExitCode(true)
            nature('python')
            pythonName('python2.7')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.shiningpanda.builders.PythonBuilder'
            children().size() == 4
            command[0].value() == 'python setup.py'
            ignoreExitCode[0].value() == true
            nature[0].value() == 'python'
            pythonName[0].value() == 'python2.7'
        }
        1 * jobManagement.requireMinimumPluginVersion('shiningpanda', '0.21')
    }

    def 'call virtualenv'() {
        when:
        context.virtualenv {
            command('python setup.py')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.shiningpanda.builders.VirtualenvBuilder'
            children().size() == 7
            command[0].value() == 'python setup.py'
            ignoreExitCode[0].value() == false
            nature[0].value() == 'shell'
            pythonName[0].value() == 'System-CPython-2.7'
        }
        1 * jobManagement.requireMinimumPluginVersion('shiningpanda', '0.21')
    }

    def 'call virtualenv with all options'() {
        when:
        context.virtualenv {
            command('python setup.py')
            clear()
            ignoreExitCode()
            name('venv')
            nature('python')
            pythonName('python2.7')
            systemSitePackages(true)
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.shiningpanda.builders.VirtualenvBuilder'
            children().size() == 7
            clear[0].value() == true
            command[0].value() == 'python setup.py'
            ignoreExitCode[0].value() == true
            home[0].value() == 'venv'
            nature[0].value() == 'python'
            pythonName[0].value() == 'python2.7'
            systemSitePackages[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('shiningpanda', '0.21')
    }

    def 'call dockerBuildAndPublish with no options'() {
        when:
        context.dockerBuildAndPublish {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'com.cloudbees.dockerpublish.DockerBuilder'
            children().size() == 15
            server[0].value().empty
            registry[0].value().empty
            repoName[0].value().empty
            noCache[0].value() == false
            forcePull[0].value() == true
            dockerfilePath[0].value().empty
            skipBuild[0].value() == false
            skipDecorate[0].value() == false
            repoTag[0].value().empty
            skipPush[0].value() == false
            createFingerprint[0].value() == true
            skipTagLatest[0].value() == false
            buildContext[0].value().empty
            buildAdditionalArgs[0].value().empty
            forceTag[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('docker-build-publish', '1.2')
    }

    def 'call dockerBuildAndPublish with all options'() {
        when:
        context.dockerBuildAndPublish {
            repositoryName('test1')
            tag('test2')
            dockerHostURI('test3')
            serverCredentials('test4')
            dockerRegistryURL('test5')
            registryCredentials('test6')
            skipPush()
            noCache()
            forcePull(false)
            skipBuild()
            createFingerprints(false)
            skipDecorate()
            skipTagAsLatest()
            dockerfileDirectory('test7')
            buildContext('test8')
            additionalBuildArgs('test9')
            forceTag(false)
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'com.cloudbees.dockerpublish.DockerBuilder'
            children().size() == 15
            server[0].children().size() == 2
            server[0].uri[0].value() == 'test3'
            server[0].credentialsId[0].value() == 'test4'
            registry[0].children().size() == 2
            registry[0].url[0].value() == 'test5'
            registry[0].credentialsId[0].value() == 'test6'
            repoName[0].value() == 'test1'
            noCache[0].value() == true
            forcePull[0].value() == false
            dockerfilePath[0].value() == 'test7'
            skipBuild[0].value() == true
            skipDecorate[0].value() == true
            repoTag[0].value() == 'test2'
            skipPush[0].value() == true
            createFingerprint[0].value() == false
            skipTagLatest[0].value() == true
            buildContext[0].value() == 'test8'
            buildAdditionalArgs[0].value() == 'test9'
            forceTag[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('docker-build-publish', '1.2')
    }

    def 'call artifactDeployer with no options'() {
        when:
        context.artifactDeployer {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerBuilder'
            children().size() == 1
            with(entry[0]) {
                children().size() == 9
                includes[0].value().empty
                basedir[0].value().empty
                remote[0].value().empty
                excludes[0].value().empty
                flatten[0].value() == false
                deleteRemote[0].value() == false
                deleteRemoteArtifacts[0].value() == false
                deleteRemoteArtifactsByScript[0].value() == false
                failNoFilesDeploy[0].value() == false
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('artifactdeployer', '0.33')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call artifactDeployer with all options'() {
        when:
        context.artifactDeployer {
            includes('test1')
            baseDir('test2')
            remoteFileLocation('test3')
            excludes('test4')
            flatten()
            cleanUp()
            deleteRemoteArtifacts()
            deleteRemoteArtifactsByScript('test5')
            failIfNoFiles()
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerBuilder'
            children().size() == 1
            with(entry[0]) {
                children().size() == 10
                includes[0].value() == 'test1'
                basedir[0].value() == 'test2'
                remote[0].value() == 'test3'
                excludes[0].value() == 'test4'
                flatten[0].value() == true
                deleteRemote[0].value() == true
                deleteRemoteArtifacts[0].value() == true
                deleteRemoteArtifactsByScript[0].value() == true
                groovyExpression[0].value() == 'test5'
                failNoFilesDeploy[0].value() == true
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('artifactdeployer', '0.33')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'call managedScript with minimal options'() {
        setup:
        jobManagement.getConfigFileId(ConfigFileType.ManagedScript, 'foo') >> '0815'

        when:
        context.managedScript('foo')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.managedscripts.ScriptBuildStep'
            children().size() == 3
            buildStepId[0].value() == '0815'
            buildStepArgs[0].value().empty
            tokenized[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('managed-scripts', '1.2.1')
    }

    def 'call managedScript with all options'() {
        setup:
        jobManagement.getConfigFileId(ConfigFileType.ManagedScript, 'foo') >> '0815'

        when:
        context.managedScript('foo') {
            arguments('foo')
            arguments('bar', 'baz')
            tokenized()
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.managedscripts.ScriptBuildStep'
            children().size() == 3
            buildStepId[0].value() == '0815'
            with(buildStepArgs[0]) {
                children().size() == 3
                string[0].value() == 'foo'
                string[1].value() == 'bar'
                string[2].value() == 'baz'
            }
            tokenized[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('managed-scripts', '1.2.1')
    }

    def 'call ruby method'() {
        when:
        context.ruby('puts \'Hello\'')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.ruby.Ruby'
            children().size() == 1
            command[0].value() == "puts 'Hello'"
        }
        1 * jobManagement.requireMinimumPluginVersion('ruby', '1.2')
    }

    def 'call nant with no options'() {
        when:
        context.nant {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.nant.NantBuilder'
            children().size() == 3
            targets[0].value().empty
            nantBuildFile[0].value().empty
            nantName[0].value() == '(Default)'
        }
        1 * jobManagement.requireMinimumPluginVersion('nant', '1.4.3')

    }

    def 'call nant with all options'() {
        when:
        context.nant {
            target('test')
            target('integTest')
            targets(['publish', 'deploy'])
            prop('test.size', 4)
            prop('logging', 'info')
            props('test.threads': 10, 'input.status': 'release')
            buildFile('dir1/nant.build')
            nantInstallation('NAnt 1.7')
        }

        then:
        context.stepNodes.size() == 1
        with (context.stepNodes[0]) {
            name() == 'hudson.plugins.nant.NantBuilder'
            children().size() == 4
            nantBuildFile[0].value() == 'dir1/nant.build'
            nantName[0].value() == 'NAnt 1.7'
            targets[0].value() == 'test integTest publish deploy'
            properties[0].value() == 'test.size=4\nlogging=info\ntest.threads=10\ninput.status=release'
        }
        1 * jobManagement.requireMinimumPluginVersion('nant', '1.4.3')
    }

    def 'call jsLint with no options'() {
        when:
        context.jsLint {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'com.boxuk.jenkins.jslint.JSLintBuilder'
            children().size() == 4
            includePattern[0].value().empty
            excludePattern[0].value().empty
            logfile[0].value().empty
            arguments[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('jslint', '0.8.2')
    }

    def 'call jsLint with all options'() {
        when:
        context.jsLint {
            includePattern('**/*.js')
            excludePattern('**/*Tests.js')
            logFile('target/jslint.xml')
            arguments('-Dadsafe=true, -Dcontinue=true')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'com.boxuk.jenkins.jslint.JSLintBuilder'
            children().size() == 4
            includePattern[0].value() == '**/*.js'
            excludePattern[0].value() == '**/*Tests.js'
            logfile[0].value() == 'target/jslint.xml'
            arguments[0].value() == '-Dadsafe=true, -Dcontinue=true'
        }
        1 * jobManagement.requireMinimumPluginVersion('jslint', '0.8.2')
    }

    def 'call progressJiraIssues with no options'() {
        when:
        context.progressJiraIssues {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.jira.JiraIssueUpdateBuilder'
            children().size() == 3
            jqlSearch[0].value().empty
            workflowActionName[0].value().empty
            comment[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')
    }

    def 'call progressJiraIssues with all options'() {
        when:
        context.progressJiraIssues {
            jqlSearch(jql)
            workflowActionName(work)
            comment(com)
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.jira.JiraIssueUpdateBuilder'
            children().size() == 3
            jqlSearch[0].value() == expectedJql
            workflowActionName[0].value() == expectedWork
            comment[0].value() == expectedCom
        }
        1 * jobManagement.requireMinimumPluginVersion('jira', '1.39')

        where:
        jql    | work   | com    | expectedJql | expectedWork | expectedCom
        null   | null   | null   | ''          | ''           | ''
        'key'  | null   | null   | 'key'       | ''           | ''
        'key1' | 'key2' | null   | 'key1'      | 'key2'       | ''
        'key1' | 'key2' | 'key3' | 'key1'      | 'key2'       | 'key3'
    }

    def 'call extractJiraEnvironmentVariables method'() {
        when:
        context.extractJiraEnvironmentVariables()

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        context.stepNodes[0].name() == 'hudson.plugins.jira.JiraEnvironmentVariableBuilder'
        context.stepNodes[0].children().size() == 0
        1 * jobManagement.requireMinimumPluginVersion('jira', '2.2')
    }

    def 'call cmake methods with no options'() {
        when:
        context.cmake {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            children().size() == 3
            generator[0].value() == 'Unix Makefiles'
            cleanBuild[0].value() == false
            installationName[0].value() == 'InSearchPath'
        }
        1 * jobManagement.requireMinimumPluginVersion('cmakebuilder', '2.4.1')
    }

    def 'call cmake methods with all options'() {
        when:
        context.cmake {
            cmakeInstallation('CMake 2.8.0')
            generator('other')
            cleanBuild()
            sourceDir('src')
            buildDir('target')
            buildType('Debug')
            preloadScript('foo')
            args('bar')
            args('foo')
            buildToolStep {
            }
            buildToolStep {
                useCmake()
                vars('A', 'B')
                vars('C', true)
                args('args1')
                args('args2')
            }
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            children().size() == 9
            installationName[0].value() == 'CMake 2.8.0'
            generator[0].value() == 'other'
            cleanBuild[0].value() == true
            sourceDir[0].value() == 'src'
            workingDir[0].value() == 'target'
            buildType[0].value() == 'Debug'
            preloadScript[0].value() == 'foo'
            toolArgs[0].value() == 'bar\nfoo'
            toolSteps[0].children().size() == 2
            with(toolSteps[0].'hudson.plugins.cmake.BuildToolStep'[0]) {
                children().size() == 1
                withCmake[0].value() == false
            }
            with(toolSteps[0].'hudson.plugins.cmake.BuildToolStep'[1]) {
                children().size() == 3
                withCmake[0].value() == true
                args[0].value() == 'args1 args2'
                vars[0].value() == 'A=B\nC=true'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('cmakebuilder', '2.4.1')
    }

    def 'call exportRuntimeParameters method'() {
        when:
        context.exportRuntimeParameters()

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'com.meyling.hudson.plugin.job__exporter.ExporterBuilder'
            children().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('job-exporter', '0.4')
    }

    def 'call jython method'() {
        when:
        context.jython('print "Hello" + "Goodbye"')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jvnet.hudson.plugins.Jython'
            children().size() == 1
            command[0].value() == 'print "Hello" + "Goodbye"'
        }
        1 * jobManagement.requireMinimumPluginVersion('jython', '1.9')
    }

    def 'call msbuild methods with no options'() {
        when:
        context.msBuild {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.msbuild.MsBuildBuilder'
            children().size() == 6
            cmdLineArgs[0].value() == ''
            buildVariablesAsProperties[0].value() == false
            continueOnBuildFailure[0].value() == false
            unstableIfWarnings[0].value() == false
            msBuildFile[0].value() == ''
            msBuildName[0].value() == '(Default)'
        }
        1 * jobManagement.requireMinimumPluginVersion('msbuild', '1.25')
    }

    def 'call msbuild methods with all options'() {
        when:
        context.msBuild {
            args('clean')
            args('build')
            buildFile('bf')
            passBuildVariables()
            continueOnBuildFailure()
            unstableIfWarnings()
            msBuildInstallation('MSBuild')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.msbuild.MsBuildBuilder'
            children().size() == 6
            cmdLineArgs[0].value() == 'clean build'
            buildVariablesAsProperties[0].value() == true
            continueOnBuildFailure[0].value() == true
            unstableIfWarnings[0].value() == true
            msBuildFile[0].value() == 'bf'
            msBuildName[0].value() == 'MSBuild'
        }
        1 * jobManagement.requireMinimumPluginVersion('msbuild', '1.25')
    }

    def 'call phing methods with no options'() {
        when:
        context.phing {
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.phing.PhingBuilder'
            children().size() == 2
            useModuleRoot[0].value() == true
            name[0].value() == '(Default)'
        }
        1 * jobManagement.requireMinimumPluginVersion('phing', '0.13.3')
    }

    def 'call phing with all options'() {
        when:
        context.phing {
            phingInstallation('Phing 2.8.0')
            useModuleRoot(false)
            buildFile('src/bf')
            targets('bar')
            targets('foo')
            properties('A', 'B')
            properties('C', true)
            options('options1')
            options('options2')
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.plugins.phing.PhingBuilder'
            children().size() == 6
            name[0].value() == 'Phing 2.8.0'
            useModuleRoot[0].value() == false
            buildFile[0].value() == 'src/bf'
            targets[0].value() == 'bar\nfoo'
            properties[0].value() == 'A=B\nC=true'
            options[0].value() == 'options1\noptions2'
        }
        1 * jobManagement.requireMinimumPluginVersion('phing', '0.13.3')
    }
}
