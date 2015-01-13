package javaposse.jobdsl.dsl.helpers.step

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification
import spock.lang.Unroll

import static javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation.LocalToWorkspace
import static javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir.WORKSPACE

class StepContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    StepContext context = new StepContext(jobManagement)

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

        when:
        context.gradle('build', '-I init.gradle', false)

        then:
        context.stepNodes.size() == 2
        def gradleStep2 = context.stepNodes[1]
        gradleStep2.switches[0].value() == '-I init.gradle'
        gradleStep2.useWrapper[0].value() == false

        when:
        context.gradle('build', '-I init.gradle', false) {
            it / 'node1' << 'value1'
        }

        then:
        context.stepNodes.size() == 3
        def gradleStep3 = context.stepNodes[2]
        gradleStep3.node1[0].value() == 'value1'
    }

    def 'call gradle methods with defaults'() {
        when:
        context.gradle()

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            tasks[0].value() == ''
            switches[0].value() == ''
            useWrapper[0].value() == true
            description[0].value() == ''
            rootBuildScriptDir[0].value() == ''
            buildFile[0].value() == ''
            gradleName[0].value() == '(Default)'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == false
        }

        when:
        context.gradle {
        }

        then:
        context.stepNodes.size() == 2
        with(context.stepNodes[1]) {
            tasks[0].value() == ''
            switches[0].value() == ''
            useWrapper[0].value() == true
            description[0].value() == ''
            rootBuildScriptDir[0].value() == ''
            buildFile[0].value() == ''
            gradleName[0].value() == '(Default)'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == false
        }
    }

    def 'call gradle methods with context'() {
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
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            tasks[0].value() == 'clean build'
            switches[0].value() == '--info --stacktrace'
            useWrapper[0].value() == false
            description[0].value() == 'desc'
            rootBuildScriptDir[0].value() == 'rbsd'
            buildFile[0].value() == 'bf'
            gradleName[0].value() == 'gn'
            fromRootBuildScriptDir[0].value() == true
            makeExecutable[0].value() == true
        }
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
        grailsStep0.useWrapper[0].value() == 'false'
        grailsStep0.grailsWorkDir[0].value() == ''
        grailsStep0.projectWorkDir[0].value() == ''
        grailsStep0.projectBaseDir[0].value() == ''
        grailsStep0.serverPort[0].value() == ''
        grailsStep0.'properties'[0].value() == ''
        grailsStep0.forceUpgrade[0].value() == 'false'
        grailsStep0.nonInteractive[0].value() == 'true'

        when:
        context.grails('compile', true)

        then:
        context.stepNodes.size() == 2
        def grailsStep1 = context.stepNodes[1]
        grailsStep1.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep1.targets[0].value() == 'compile'
        grailsStep1.useWrapper[0].value() == 'true'
        grailsStep1.grailsWorkDir[0].value() == ''
        grailsStep1.projectWorkDir[0].value() == ''
        grailsStep1.projectBaseDir[0].value() == ''
        grailsStep1.serverPort[0].value() == ''
        grailsStep1.'properties'[0].value() == ''
        grailsStep1.forceUpgrade[0].value() == 'false'
        grailsStep1.nonInteractive[0].value() == 'true'

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
        grailsStep2.useWrapper[0].value() == 'false'
        grailsStep2.grailsWorkDir[0].value() == 'work1'
        grailsStep2.projectWorkDir[0].value() == ''
        grailsStep2.projectBaseDir[0].value() == ''
        grailsStep2.serverPort[0].value() == ''
        grailsStep2.'properties'[0].value() == ''
        grailsStep2.forceUpgrade[0].value() == 'false'
        grailsStep2.nonInteractive[0].value() == 'false'

        when:
        context.grails {
            target 'clean'
            targets(['compile', 'test-app'])
            useWrapper true
            grailsWorkDir 'work'
            projectWorkDir 'project'
            projectBaseDir  'base'
            serverPort  '1111'
            props  prop1: 'val1', prop2: 'val2'
            prop 'prop3', 'val3'
            forceUpgrade  true
            nonInteractive  false
        }

        then:
        context.stepNodes.size() == 4
        def grailsStep3 = context.stepNodes[3]
        grailsStep3.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep3.targets[0].value() == 'clean compile test-app'
        grailsStep3.useWrapper[0].value() == 'true'
        grailsStep3.grailsWorkDir[0].value() == 'work'
        grailsStep3.projectWorkDir[0].value() == 'project'
        grailsStep3.projectBaseDir[0].value() == 'base'
        grailsStep3.serverPort[0].value() == '1111'
        grailsStep3.'properties'[0].value() == 'prop1=val1\nprop2=val2\nprop3=val3'
        grailsStep3.forceUpgrade[0].value() == 'true'
        grailsStep3.nonInteractive[0].value() == 'false'

        when:
        context.grails '"test-app --stacktrace"', {
            useWrapper true
            grailsWorkDir 'work'
            projectWorkDir 'project'
            projectBaseDir  'base'
            serverPort  '8080'
            forceUpgrade  true
            nonInteractive  false
        }

        then:
        context.stepNodes.size() == 5
        def grailsStep4 = context.stepNodes[4]
        grailsStep4.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep4.targets[0].value() == '"test-app --stacktrace"'
        grailsStep4.useWrapper[0].value() == 'true'
        grailsStep4.grailsWorkDir[0].value() == 'work'
        grailsStep4.projectWorkDir[0].value() == 'project'
        grailsStep4.projectBaseDir[0].value() == 'base'
        grailsStep4.serverPort[0].value() == '8080'
        grailsStep4.'properties'[0].value() == ''
        grailsStep4.forceUpgrade[0].value() == 'true'
        grailsStep4.nonInteractive[0].value() == 'false'

        when:
        context.grails {
        }

        then:
        context.stepNodes.size() == 6
        def grailsStep5 = context.stepNodes[5]
        grailsStep5.name() == 'com.g2one.hudson.grails.GrailsBuilder'
        grailsStep5.targets[0].value() == ''
        grailsStep5.useWrapper[0].value() == 'false'
        grailsStep5.grailsWorkDir[0].value() == ''
        grailsStep5.projectWorkDir[0].value() == ''
        grailsStep5.projectBaseDir[0].value() == ''
        grailsStep5.serverPort[0].value() == ''
        grailsStep5.'properties'[0].value() == ''
        grailsStep5.forceUpgrade[0].value() == 'false'
        grailsStep5.nonInteractive[0].value() == 'true'
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
            localRepository(LocalToWorkspace)
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
        mavenStep.usePrivateRepository[0].value() == 'true'
        mavenStep.mavenName[0].value() == 'Maven 3.0.5'
        mavenStep.settingsConfigId[0].value() == 'foo-bar'
        mavenStep.properties[0].value() == 'skipTests=true\nother=some\nevenAnother=One'
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
        mavenStep.usePrivateRepository[0].value() == 'false'
        mavenStep.mavenName[0].value() == '(Default)'
    }

    def 'call maven method with unknown provided settings'() {
        setup:
        String settingsName = 'lalala'

        when:
        context.maven {
            providedSettings(settingsName)
        }

        then:
        Exception e = thrown(NullPointerException)
        e.message.contains(settingsName)
    }

    def 'call maven method with provided settings'() {
        setup:
        String settingsName = 'maven-proxy'
        String settingsId = '123123415'
        jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName) >> settingsId

        when:
        context.maven {
            providedSettings(settingsName)
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'hudson.tasks.Maven'
            children().size() == 5
            targets[0].value() == ''
            jvmOptions[0].value() == ''
            usePrivateRepository[0].value() == 'false'
            mavenName[0].value() == '(Default)'
            settings[0].attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
            settings[0].children().size() == 1
            settings[0].settingsConfigId[0].value() == settingsId
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

        when:
        context.ant('build')

        then:
        context.stepNodes.size() == 2
        def antBuildNode = context.stepNodes[1]
        antBuildNode.targets[0].value() == 'build'

        when:
        context.ant('build', 'dir1/build.xml', 'Ant 1.8')

        then:
        context.stepNodes.size() == 3
        def antArgs = context.stepNodes[2]
        antArgs.buildFile[0].value() == 'dir1/build.xml'
        antArgs.antName[0].value() == 'Ant 1.8'

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
        acmeGroovyNode.parameters[0].value() == 'foo\nbar\nbaz'
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == 'alfa\nbravo\ncharlie'
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == 'one=two\nthree=four\nfive=six'
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == 'acme.Acme.doSomething()'
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
        acmeGroovyNode.parameters[0].value() == 'foo\nbar\nbaz'
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == 'alfa\nbravo\ncharlie'
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == 'one=two\nthree=four\nfive=six'
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.FileScriptSource'
        acmeScriptSourceNode.scriptFile.size() == 1
        acmeScriptSourceNode.scriptFile[0].value() == 'acme.groovy'

        when:
        context.groovyScriptFile('foo.groovy') {
            groovyInstallation('Groovy 2.1')
        }

        then:
        context.stepNodes.size() == 3
        def groovy21Node = context.stepNodes[2]
        groovy21Node.groovyName.size() == 1
        groovy21Node.groovyName[0].value() == 'Groovy 2.1'
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
    }

    def 'call minimal copyArtifacts'() {
        when: 'Least arguments'
        context.copyArtifacts('upstream', '**/*.xml') {
            upstreamBuild()
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.26')
        context.stepNodes.size() == 1
        def copyEmptyNode = context.stepNodes[0]
        copyEmptyNode.name() == 'hudson.plugins.copyartifact.CopyArtifact'
        copyEmptyNode.flatten.size() == 0
        copyEmptyNode.optional.size() == 0
        copyEmptyNode.filter[0].value() == '**/*.xml'
        copyEmptyNode.target[0] != null
        copyEmptyNode.target[0].value() == ''
        Node selectorNode = copyEmptyNode.selector[0]
        selectorNode.attribute('class') == 'hudson.plugins.copyartifact.TriggeredBuildSelector'
        selectorNode.children().size() == 0
    }

    def 'call copyArtifacts all args'() {
        when:
        context.copyArtifacts('upstream', '**/*.xml', 'target/', true, true) {
            upstreamBuild(true)
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('copyartifact', '1.26')
        context.stepNodes.size() == 1
        def copyEmptyNode = context.stepNodes[0]
        copyEmptyNode.name() == 'hudson.plugins.copyartifact.CopyArtifact'
        copyEmptyNode.flatten[0].value() == 'true'
        copyEmptyNode.optional[0].value() == 'true'
        copyEmptyNode.target[0].value() == 'target/'
        Node selectorNode = copyEmptyNode.selector[0]
        selectorNode.attribute('class') == 'hudson.plugins.copyartifact.TriggeredBuildSelector'
        selectorNode.fallbackToLastSuccessful[0].value() == 'true'
    }

    def 'call copyArtifacts selector variants'() {
        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            latestSuccessful()
        }

        then:
        Node selectorNode = context.stepNodes[0].selector[0]
        selectorNode.attribute('class') == 'hudson.plugins.copyartifact.StatusBuildSelector'
        selectorNode.children().size() == 0

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            latestSaved()
        }

        then:
        def selectorNode2 = context.stepNodes[1].selector[0]
        selectorNode2.attribute('class') == 'hudson.plugins.copyartifact.SavedBuildSelector'
        selectorNode2.children().size() == 0

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            permalink('lastBuild')
        }

        then:
        def selectorNode3 = context.stepNodes[2].selector[0]
        selectorNode3.attribute('class') == 'hudson.plugins.copyartifact.PermalinkBuildSelector'
        selectorNode3.id[0].value() == 'lastBuild'

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            buildNumber(43)
        }

        then:
        def selectorNode4 = context.stepNodes[3].selector[0]
        selectorNode4.attribute('class') == 'hudson.plugins.copyartifact.SpecificBuildSelector'
        selectorNode4.buildNumber[0].value() == '43'

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            workspace()
        }

        then:
        def selectorNode5 = context.stepNodes[4].selector[0]
        selectorNode5.attribute('class') == 'hudson.plugins.copyartifact.WorkspaceSelector'
        selectorNode5.children().size() == 0

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            buildParameter('BUILD_PARAM')
        }

        then:
        def selectorNode6 = context.stepNodes[5].selector[0]
        selectorNode6.attribute('class') == 'hudson.plugins.copyartifact.ParameterizedBuildSelector'
        selectorNode6.parameterName[0].value() == 'BUILD_PARAM'

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            buildNumber('$SOME_PARAMTER')
        }

        then:
        def selectorNode7 = context.stepNodes[6].selector[0]
        selectorNode7.attribute('class') == 'hudson.plugins.copyartifact.SpecificBuildSelector'
        selectorNode7.buildNumber[0].value() == '$SOME_PARAMTER'

        when:
        context.copyArtifacts('upstream', '**/*.xml') {
            latestSuccessful(true)
        }

        then:
        Node selectorNode8 = context.stepNodes[7].selector[0]
        selectorNode8.attribute('class') == 'hudson.plugins.copyartifact.StatusBuildSelector'
        selectorNode8.children().size() == 1
        selectorNode8.stable[0].value() == 'true'

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
    }

    def 'call resolveArtifacts with all arguments and two artifacts' () {
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
    }

    def 'call phases with minimal arguments'() {
        when:
        context.phase('First')

        then:
        def phaseNode = context.stepNodes[0]
        phaseNode.name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
        phaseNode.phaseName[0].value() == 'First'
        phaseNode.continuationCondition[0].value() == 'SUCCESSFUL'

        when:
        context.phase {
            phaseName 'Second'
            job('JobA')
        }

        then:
        def phaseNode2 = context.stepNodes[1]
        phaseNode2.phaseName[0].value() == 'Second'
        def jobNode = phaseNode2.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 4
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == true
        jobNode.exposedSCM[0].value() == true
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
    }

    def 'call phases with minimal arguments and plugin version 1.11'() {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber('1.11')

        when:
        context.phase {
            phaseName 'Second'
            job('JobA')
        }

        then:
        def phaseNode = context.stepNodes[0]
        phaseNode.phaseName[0].value() == 'Second'
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 6
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == true
        jobNode.exposedSCM[0].value() == true
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        jobNode.disableJob[0].value() == false
        jobNode.killPhaseOnJobResultCondition[0].value() == 'FAILURE'
    }

    def 'call phases with multiple jobs'() {
        when:
        context.phase('Third') {
            job('JobA')
            job('JobB')
            job('JobC')
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNodeA = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNodeA.jobName[0].value() == 'JobA'
        def jobNodeB = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[1]
        jobNodeB.jobName[0].value() == 'JobB'
        def jobNodeC = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[2]
        jobNodeC.jobName[0].value() == 'JobC'
    }

    def 'call phases with jobs with complex parameters'() {
        when:
        context.phase('Fourth') {
            job('JobA', false, true) {
                boolParam('aParam')
                boolParam('bParam', false)
                boolParam('cParam', true)
                fileParam('my.properties')
                sameNode()
                matrixParam('it.name=="hello"')
                subversionRevision()
                gitRevision()
                prop('prop1', 'value1')
                prop('prop2', 'value2')
                props([
                        prop3: 'value3',
                        prop4: 'value4'
                ])
                nodeLabel('nodeParam', 'node_label')
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.currParams[0].value() == false
        jobNode.exposedSCM[0].value() == true
        def configsNode = jobNode.configs[0]
        def boolParams = configsNode.'hudson.plugins.parameterizedtrigger.BooleanParameters'[0].configs[0]
        boolParams.children().size() == 3
        def boolNode = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]
        boolNode.name[0].value() == 'aParam'
        boolNode.value[0].value() == false
        def boolNode1 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]
        boolNode1.name[0].value() == 'bParam'
        boolNode1.value[0].value() == false
        def boolNode2 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]
        boolNode2.name[0].value() == 'cParam'
        boolNode2.value[0].value() == true

        def fileNode = configsNode.'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0]
        fileNode.propertiesFile[0].value() == 'my.properties'
        fileNode.failTriggerOnMissing[0].value() == false

        def nodeNode = configsNode.'hudson.plugins.parameterizedtrigger.NodeParameters'[0]
        nodeNode != null

        def matrixNode = configsNode.'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0]
        matrixNode.filter[0].value() == 'it.name=="hello"'

        def svnNode = configsNode.'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0]
        svnNode.includeUpstreamParameters[0].value() == false

        def gitNode = configsNode.'hudson.plugins.git.GitRevisionBuildParameters'[0]
        gitNode.combineQueuedCommits[0].value() == false

        def propNode = configsNode.'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0]
        def propStr = propNode.'properties'[0].value()
        propStr.contains('prop1=value1')
        propStr.contains('prop2=value2')
        propStr.contains('prop3=value3')
        propStr.contains('prop4=value4')

        def nodeLabel = configsNode.
            'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0]
        nodeLabel.name[0].value() == 'nodeParam'
        nodeLabel.nodeLabel[0].value() == 'node_label'
    }

    def 'call phases with multiple calls'() {
        when:
        context.phase('Third') {
            job('JobA') {
                fileParam('my1.properties')
                fileParam('my2.properties')
            }
        }

        then:
        thrown(IllegalStateException)

        when:
        context.phase('Third') {
            job('JobA') {
                matrixParam('it.size=2')
                matrixParam('it.size=3')
            }
        }

        then:
        thrown(IllegalStateException)
    }

    def 'call phases with plugin version 1.11 options'() {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber('1.11')

        when:
        context.phase {
            phaseName 'Second'
            job('JobA') {
                disableJob()
                killPhaseCondition('UNSTABLE')
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        phaseNode.phaseName[0].value() == 'Second'
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 6
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == true
        jobNode.exposedSCM[0].value() == true
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        jobNode.disableJob[0].value() == true
        jobNode.killPhaseOnJobResultCondition[0].value() == 'UNSTABLE'
    }

    def 'call killPhaseCondition with invalid argument'() {
        when:
        context.phase {
            phaseName 'Second'
            job('JobA') {
                killPhaseCondition('UNKNOWN')
            }
        }

        then:
        thrown(IllegalArgumentException)
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
    }

    def 'call dsl method defaults' () {
        when:
        context.dsl {
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == ''
        dslStep.usingScriptText[0].value() == false
        dslStep.ignoreExisting[0].value() ==  false
        dslStep.removedJobAction[0].value() == 'IGNORE'
        dslStep.scriptText[0].value() == ''
        dslStep.additionalClasspath[0].value() == ''
    }

    def 'call dsl method external script ignoring existing' () {
        when:
        context.dsl {
            removeAction 'DISABLE'
            external 'some-dsl.groovy', 'some-other-dsl.groovy'
            external 'still-another-dsl.groovy'
            ignoreExisting()
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == '''some-dsl.groovy
some-other-dsl.groovy
still-another-dsl.groovy'''
        dslStep.usingScriptText[0].value() == false
        dslStep.ignoreExisting[0].value() ==  true
        dslStep.removedJobAction[0].value() == 'DISABLE'
        dslStep.scriptText[0].value() == ''
    }

    def 'call dsl method external script' () {
        when:
        context.dsl {
            removeAction 'DISABLE'
            external 'some-dsl.groovy', 'some-other-dsl.groovy'
            external 'still-another-dsl.groovy'
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == '''some-dsl.groovy
some-other-dsl.groovy
still-another-dsl.groovy'''
        dslStep.usingScriptText[0].value() == false
        dslStep.ignoreExisting[0].value() ==  false
        dslStep.removedJobAction[0].value() == 'DISABLE'
        dslStep.scriptText[0].value() == ''
    }

    def 'call dsl method with script text' () {
        when:
        context.dsl {
            removeAction('DELETE')
            text '''job {
  foo()
  bar {
    baz()
  }
}
'''
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == ''
        dslStep.usingScriptText[0].value() == true
        dslStep.ignoreExisting[0].value() ==  false
        dslStep.removedJobAction[0].value() == 'DELETE'
        dslStep.scriptText[0].value() == '''job {
  foo()
  bar {
    baz()
  }
}
'''
    }

    def 'call dsl method external script as parameters' () {
        when:
        context.dsl(['some-dsl.groovy', 'some-other-dsl.groovy', 'still-another-dsl.groovy'], 'DISABLE')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == '''some-dsl.groovy
some-other-dsl.groovy
still-another-dsl.groovy'''
        dslStep.usingScriptText[0].value() == false
        dslStep.ignoreExisting[0].value() ==  false
        dslStep.removedJobAction[0].value() == 'DISABLE'
        dslStep.scriptText[0].value() == ''
    }

    def 'call dsl method external script as parameters full' () {
        when:
        context.dsl(['some-dsl.groovy', 'some-other-dsl.groovy', 'still-another-dsl.groovy'], 'DISABLE', true)

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == '''some-dsl.groovy
some-other-dsl.groovy
still-another-dsl.groovy'''
        dslStep.usingScriptText[0].value() == false
        dslStep.ignoreExisting[0].value() ==  true
        dslStep.removedJobAction[0].value() == 'DISABLE'
        dslStep.scriptText[0].value() == ''
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
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == ''
        dslStep.usingScriptText[0].value() == true
        dslStep.ignoreExisting[0].value() ==  false
        dslStep.removedJobAction[0].value() == 'DELETE'
        dslStep.scriptText[0].value() == '''job {
  foo()
  bar {
    baz()
  }
}
'''
    }

    def 'call dsl method with additional classpath' () {
        when:
        context.dsl {
            external 'some-dsl.groovy'
            additionalClasspath 'some/path'
        }

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def dslStep = context.stepNodes[0]
        dslStep.name() == 'javaposse.jobdsl.plugin.ExecuteDslScripts'
        dslStep.targets[0].value() == 'some-dsl.groovy'
        dslStep.usingScriptText[0].value() == false
        dslStep.ignoreExisting[0].value() ==  false
        dslStep.removedJobAction[0].value() == 'IGNORE'
        dslStep.scriptText[0].value() == ''
        dslStep.additionalClasspath[0].value() == 'some/path'
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
    }

    def 'call publishOverSsh without server'() {
        when:
        context.publishOverSsh(null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'call publishOverSsh without transferSet'() {
        when:
        context.publishOverSsh {
            server('server-name') {
            }
        }

        then:
        thrown(IllegalArgumentException)
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
        thrown(IllegalArgumentException)
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
                        with (delegate.'jenkins.plugins.publish__over__ssh.BapSshPublisher'[0]) {
                            configName[0].value() == 'server-name'
                            verbose[0].value() == false
                            with(transfers[0]) {
                                children().size() == 1
                                with (delegate.'jenkins.plugins.publish__over__ssh.BapSshTransfer'[0]) {
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
    }

    def 'call downstream build step with all args'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2', 'UNSTABLE_OR_BETTER', true,
                    [buildStepFailure: 'FAILURE', failure: 'FAILURE', unstable: 'UNSTABLE']) {
                currentBuild() // Current build parameters
                propertiesFile('dir/my.properties') // Parameters from properties file
                gitRevision(false) // Pass-through Git commit that was built
                predefinedProp('key1', 'value1') // Predefined properties
                predefinedProps([key2: 'value2', key3: 'value3'])
                predefinedProps('key4=value4\nkey5=value5') // Newline separated
                matrixSubset('label=="${TARGET}"') // Restrict matrix execution to a subset
                subversionRevision() // Subversion Revision
                nodeLabel('nodeParam', 'node_label') // Limit to node label selection
            }
            trigger('Project2') {
                currentBuild()
            }
        }

        then:
        Node stepNode = context.stepNodes[0]
        stepNode.name() == 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
        stepNode.configs[0].children().size() == 2
        with(stepNode.configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
            projects[0].value() == 'Project1, Project2'
            condition[0].value() == 'UNSTABLE_OR_BETTER'
            triggerWithNoParameters[0].value() == 'true'
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
            configs[0].'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0].propertiesFile[0].value() ==
                    'dir/my.properties'
            configs[0].'hudson.plugins.git.GitRevisionBuildParameters'[0].combineQueuedCommits[0].value() == false
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'.size() == 1
            configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0].'properties'[0].value() ==
                    'key1=value1\nkey2=value2\nkey3=value3\nkey4=value4\nkey5=value5'
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
            Node failureThreshold = thresholds.failureThreshold[0]
            failureThreshold.name[0].value() == 'FAILURE'
            failureThreshold.ordinal[0].value() == 2
            Node buildStepFailureThreshold = thresholds.buildStepFailureThreshold[0]
            buildStepFailureThreshold.name[0].value() == 'FAILURE'
        }

        with(stepNode.configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[1]) {
            projects[0].value() == 'Project2'
            condition[0].value() == 'SUCCESS'
            triggerWithNoParameters[0].value() == 'false'
            configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
            block.isEmpty()
        }

        when:
        context.downstreamParameterized {
            trigger('Project3') {
            }
        }

        then:
        with(context.stepNodes[1].configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]) {
            projects[0].value() == 'Project3'
            condition[0].value() == 'SUCCESS'
            triggerWithNoParameters[0].value() == 'false'
            configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        }

        when:
        context.downstreamParameterized {
            trigger('Project4', 'WRONG')
        }

        then:
        thrown(AssertionError)
    }

    @Unroll
    def 'call conditional steps for a single step with #testCondition'() {
        when:
        context.conditionalSteps {
            condition {
                delegate.invokeMethod(testCondition, testConditionArgs.values().toArray())
            }
            runner('Fail')
            shell('look at me')
        }

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder'
        step.condition[0].children().size() == testConditionArgs.values().size()

        Node condition = step.condition[0]
        condition.attribute('class') == "org.jenkins_ci.plugins.run_condition.core.${testConditionClass}"
        if (!testConditionArgs.isEmpty()) {
            testConditionArgs.each { k, v ->
                condition[k][0].value() == v
            }
        }
        step.runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'

        Node childStep = step.buildStep[0]
        childStep.attribute('class') == 'hudson.tasks.Shell'
        childStep.command[0].value() == 'look at me'

        where:
        testCondition << [
                'stringsMatch', 'alwaysRun', 'neverRun', 'booleanCondition', 'cause', 'expression', 'time'
        ]
        testConditionArgs << [
                ['arg1': 'foo', 'arg2': 'bar', 'ignoreCase': false], [:], [:],
                ['token': 'foo'], ['buildCause': 'foo', 'exclusiveCondition': true],
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

    @Unroll
    def 'call conditional steps for a single step with #runner'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            runner(runnerName)
            shell('look at me')
        }

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder'

        step.runner[0].attribute('class') == "org.jenkins_ci.plugins.run_condition.BuildStepRunner\$${runnerName}"

        Node childStep = step.buildStep[0]
        childStep.attribute('class') == 'hudson.tasks.Shell'
        childStep.command[0].value() == 'look at me'

        where:
        runnerName << ['Fail', 'Unstable', 'RunUnstable', 'Run', 'DontRun']
    }

    def 'call conditional steps with unknown runner'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            runner('invalid-runner')
            shell('look at me')
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'call conditional steps with no condition'() {
        when:
        context.conditionalSteps {
            condition {
            }
            runner('Fail')
            shell('look at me')
        }

        then:
        thrown(NullPointerException)
    }

    def 'call conditional steps with invalid condition'() {
        when:
        context.conditionalSteps {
            condition {
                invalidCondition()
            }
            runner('Fail')
            shell('look at me')
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
            shell('echo Test')
        }

        then:
        Node step = context.stepNodes[0]
        step.condition[0].children().size() == 1

        Node notCondition = step.condition[0]
        notCondition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.logic.Not'
        Node matchCondition = notCondition.condition[0]
        matchCondition.attribute('class') ==  'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
        matchCondition.arg1[0].value() == 'foo'
        matchCondition.arg2[0].value() == 'bar'
        matchCondition.ignoreCase[0].value() == 'false'
    }

    def 'call conditional steps for multiple steps'() {
        when:
        context.conditionalSteps {
            condition {
                stringsMatch('foo', 'bar', false)
            }
            runner('Fail')
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

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'
        step.runCondition[0].children().size() == 3

        Node condition = step.runCondition[0]
        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
        condition.arg1[0].value() == 'foo'
        condition.arg2[0].value() == 'bar'
        condition.ignoreCase[0].value() == 'false'

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
        acmeGroovyNode.parameters[0].value() == 'foo\nbar\nbaz'
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == 'alfa\nbravo\ncharlie'
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == 'one=two\nthree=four\nfive=six'
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == 'acme.Acme.doSomething()'
    }

    def 'fileExists is added correctly'() {
        when:
        context.conditionalSteps {
            condition {
                fileExists('someFile', WORKSPACE)
            }
            shell('echo Test')
        }

        then:
        Node step = context.stepNodes[0]
        step.condition[0].children().size() == 2

        Node condition = step.condition[0]
        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.FileExistsCondition'
        condition.file[0].value() == 'someFile'
        condition.baseDir[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.common.BaseDirectory$Workspace'
    }

    def 'status condition is added correctly'() {
        when:
        context.conditionalSteps {
            condition {
                status 'FAILURE', 'SUCCESS'
            }
            shell 'echo Test'
        }

        then:
        Node step = context.stepNodes[0]
        Node condition = step.condition[0]

        condition.attribute('class') == 'org.jenkins_ci.plugins.run_condition.core.StatusCondition'
        condition.children().size() == 2

        condition.worstResult[0].children().size() == 1
        condition.worstResult[0].ordinal[0].value() == 2
        condition.bestResult[0].children().size() == 1
        condition.bestResult[0].ordinal[0].value() == 0
    }

    @Unroll
    def 'Logical Operation #dslOperation is added correctly'(String dslOperation, String operation) {
        when:
        context.conditionalSteps {
            condition {
                "${dslOperation}" {
                    fileExists('someFile', WORKSPACE)
                } {
                    alwaysRun()
                }
            }
            shell('echo Test')
        }

        then:
        Node step = context.stepNodes[0]

        def logicOperation = step.condition[0]
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

        where:
        dslOperation | operation
        'and'        | 'org.jenkins_ci.plugins.run_condition.logic.And'
        'or'         | 'org.jenkins_ci.plugins.run_condition.logic.Or'
    }

    @Unroll
    @SuppressWarnings('LineLength')
    def 'Simple Condition #conditionDsl is added correctly'(conditionDsl, args, conditionClass, argNodes) {
        when:
        context.conditionalSteps {
            condition {
                "${conditionDsl}"(*args)
            }
            shell('echo something outside')
        }

        then:
        Node step = context.stepNodes[0]
        Node conditionNode = step.condition[0]
        conditionNode.children().size() == argNodes.size()

        conditionNode.attribute('class') == conditionClass
        def ignored = argNodes.each { name, value ->
            assert conditionNode[name][0].value() == value
        }

        where:
        conditionDsl       | args                     | conditionClass                                                        | argNodes
        'shell'            | ['echo test']            | 'org.jenkins_ci.plugins.run_condition.contributed.ShellCondition'     | [command: 'echo test']
        'batch'            | ['xcopy * ..\\']         | 'org.jenkins_ci.plugins.run_condition.contributed.BatchFileCondition' | [command: 'xcopy * ..\\']
        'alwaysRun'        | []                       | 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'                 | [:]
        'neverRun'         | []                       | 'org.jenkins_ci.plugins.run_condition.core.NeverRun'                  | [:]
        'booleanCondition' | ['someToken']            | 'org.jenkins_ci.plugins.run_condition.core.BooleanCondition'          | [token: 'someToken']
        'cause'            | ['userCause', true]      | 'org.jenkins_ci.plugins.run_condition.core.CauseCondition'            | [buildCause        : 'userCause',
                                                                                                                                 exclusiveCondition: 'true']
        'stringsMatch'     | ['some1', 'some2', true] | 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'     | [arg1      : 'some1',
                                                                                                                                 arg2      : 'some2',
                                                                                                                                 ignoreCase: 'true']
        'expression'       | ['exp', 'lab']           | 'org.jenkins_ci.plugins.run_condition.core.ExpressionCondition'       | [expression: 'exp',
                                                                                                                                 label     : 'lab']
        'time'             | [5, 30, 15, 25, true]    | 'org.jenkins_ci.plugins.run_condition.core.TimeCondition'             | [earliestHours  : 5,
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
            shell('echo something outside')
        }

        then:
        thrown(IllegalArgumentException)

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
    }

    def 'call remoteTrigger with minimal options'() {
        when:
        context.remoteTrigger('dev-ci', 'test')

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration'
            children().size() == 14
            token[0].value() == []
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
    }

    def 'call remoteTrigger with parameters'() {
        when:
        context.remoteTrigger('dev-ci', 'test') {
            parameter 'foo', '1'
            parameters bar: '2', baz: '3'
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration'
            children().size() == 14
            token[0].value() == []
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
    }

    def 'call remoteTrigger without jenkins'() {
        when:
        context.remoteTrigger(null, 'test')

        then:
        thrown(IllegalArgumentException)

        when:
        context.remoteTrigger('', 'test')

        then:
        thrown(IllegalArgumentException)
    }

    def 'call remoteTrigger without job'() {
        when:
        context.remoteTrigger('dev-ci', null)

        then:
        thrown(IllegalArgumentException)

        when:
        context.remoteTrigger('dev-ci', '')

        then:
        thrown(IllegalArgumentException)
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
    }

    def 'vSphere server not found'() {
        when:
        context.vSpherePowerOff('vsphere.acme.org', 'foo')

        then:
        thrown(NullPointerException)
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
    }

    def 'call http request with all options'() {
        when:
        context.httpRequest('http://www.example.com') {
            httpMode('GET')
            authentication('bob')
            returnCodeBuildRelevant()
            logResponseBody()
        }

        then:
        context.stepNodes.size() == 1
        with(context.stepNodes[0]) {
            name() == 'jenkins.plugins.http__request.HttpRequest'
            children().size() == 5
            url[0].value() == 'http://www.example.com'
            httpMode[0].value() == 'GET'
            authentication[0].value() == 'bob'
            returnCodeBuildRelevant[0].value() == true
            logResponseBody[0].value() == true
        }
    }

    def 'call http request with invalid HTTP mode'() {
        when:
        context.httpRequest('http://www.example.com') {
            httpMode('WHAT')
        }

        then:
        thrown(IllegalArgumentException)
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

        where:
        mode << ['GET', 'POST', 'PUT', 'DELETE']
    }
}
