package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification
import spock.lang.Unroll

public class StepHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    StepContextHelper helper = new StepContextHelper(mockActions, JobType.Freeform)
    StepContext context = new StepContext(JobType.Freeform)

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
        gradleStep.useWrapper[0].value() == 'true'

        when:
        context.gradle('build', '-I init.gradle', false)

        then:
        context.stepNodes.size() == 2
        def gradleStep2 = context.stepNodes[1]
        gradleStep2.switches[0].value() == '-I init.gradle'
        gradleStep2.useWrapper[0].value() == 'false'
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
        context.grails {}

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
            def nameNode = mavenNode/mavenName
            nameNode.value = 'Maven 2.0.1'
        }

        then:
        context.stepNodes.size() == 2
        def mavenStep2 = context.stepNodes[1]
        mavenStep2.pom[0].value() == 'pom.xml'
        mavenStep2.mavenName[0].value() == 'Maven 2.0.1'
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
            targets(['publish', 'deploy']) // FIXME: I have no idea why the parens are needed
            prop 'test.size', 4
            prop 'logging', 'info'
            props 'test.threads': 10, 'input.status':'release'
            buildFile 'dir2/build.xml'
            buildFile 'dir1/build.xml'
            javaOpt '-Xmx1g'
            javaOpts(['-Dprop2=value2', '-Dprop3=value3']) // FIXME: I have no idea why the parens are needed
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
        acmeGroovyNode.parameters[0].value() == "foo\nbar\nbaz"
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == "alfa\nbravo\ncharlie"
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == "one=two\nthree=four\nfive=six"
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
        acmeGroovyNode.parameters[0].value() == "foo\nbar\nbaz"
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == "alfa\nbravo\ncharlie"
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == "one=two\nthree=four\nfive=six"
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
        context.systemGroovyCommand("acme.Acme.doSomething()") {
            binding("foo", "bar")
            binding("test", "0815")
            classpath("/foo/acme.jar")
            classpath("/foo/test.jar")
        }

        then:
        context.stepNodes.size() == 2
        def acmeSystemGroovyNode = context.stepNodes[1]
        acmeSystemGroovyNode.name() == 'hudson.plugins.groovy.SystemGroovy'
        acmeSystemGroovyNode.bindings.size() == 1
        acmeSystemGroovyNode.bindings[0].value() == "foo=bar\ntest=0815"
        acmeSystemGroovyNode.classpath.size() == 1
        acmeSystemGroovyNode.classpath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeSystemGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeSystemGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == "acme.Acme.doSomething()"
    }

    def 'call systemGroovyScriptFile methods'() {
        when:
        context.systemGroovyScriptFile("scripts/hello.groovy")

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
        scriptSourceNode.scriptFile[0].value() == "scripts/hello.groovy"

        when:
        context.systemGroovyScriptFile("acme.groovy") {
            binding("foo", "bar")
            binding("test", "0815")
            classpath("/foo/acme.jar")
            classpath("/foo/test.jar")
        }

        then:
        context.stepNodes.size() == 2
        def acmeSystemGroovyNode = context.stepNodes[1]
        acmeSystemGroovyNode.name() == 'hudson.plugins.groovy.SystemGroovy'
        acmeSystemGroovyNode.bindings.size() == 1
        acmeSystemGroovyNode.bindings[0].value() == "foo=bar\ntest=0815"
        acmeSystemGroovyNode.classpath.size() == 1
        acmeSystemGroovyNode.classpath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeSystemGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeSystemGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.FileScriptSource'
        acmeScriptSourceNode.scriptFile.size() == 1
        acmeScriptSourceNode.scriptFile[0].value() == "acme.groovy"
    }

    def 'call minimal copyArtifacts'() {
        when: 'Least arguments'
        context.copyArtifacts('upstream', '**/*.xml') {
            upstreamBuild()
        }

        then:
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
        context.phase() {
            phaseName 'Second'
            job('JobA')
        }

        then:
        def phaseNode2 = context.stepNodes[1]
        phaseNode2.phaseName[0].value() == 'Second'
        def jobNode = phaseNode2.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == 'true'
        jobNode.exposedSCM[0].value() == 'true'
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
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
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.currParams[0].value() == 'false'
        jobNode.exposedSCM[0].value() == 'true'
        def configsNode = jobNode.configs[0]
        def boolParams = configsNode.'hudson.plugins.parameterizedtrigger.BooleanParameters'[0].configs[0]
        boolParams.children().size() == 3
        def boolNode = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]
        boolNode.name[0].value() == 'aParam'
        boolNode.value[0].value() == 'false'
        def boolNode1 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]
        boolNode1.name[0].value() == 'bParam'
        boolNode1.value[0].value() == 'false'
        def boolNode2 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]
        boolNode2.name[0].value() == 'cParam'
        boolNode2.value[0].value() == 'true'

        def fileNode = configsNode.'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0]
        fileNode.propertiesFile[0].value() == 'my.properties'
        fileNode.failTriggerOnMissing[0].value() == 'false'

        def nodeNode = configsNode.'hudson.plugins.parameterizedtrigger.NodeParameters'[0]
        nodeNode != null

        def matrixNode = configsNode.'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0]
        matrixNode.filter[0].value() == 'it.name=="hello"'

        def svnNode = configsNode.'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0]
        svnNode.includeUpstreamParameters[0].value() == 'false'

        def gitNode = configsNode.'hudson.plugins.git.GitRevisionBuildParameters'[0]
        gitNode.combineQueuedCommits[0].value() == 'false'

        def propNode = configsNode.'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0]
        def propStr = propNode.'properties'[0].value()
        propStr.contains('prop1=value1')
        propStr.contains('prop2=value2')
        propStr.contains('prop3=value3')
        propStr.contains('prop4=value4')
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

    def 'call step via helper'() {
        when:
        helper.steps {
            shell('ls')
            gradle('build')
        }

        then:
        1 * mockActions.add(_)

        // TODO Support this notation
//        when:
//        helper.steps.shell('ls')
//
//        then:
//        1 * mockActions.add(_)
    }

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        def nodeBuilder = new NodeBuilder()

        Node stepNode = nodeBuilder.'hudson.tasks.Shell' {
            command 'ls'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction(new StepContext([stepNode], JobType.Freeform))
        withXmlAction.execute(root)

        then:
        root.builders[0].'hudson.tasks.Shell'[0].command[0].text() == 'ls'
    }

    def 'no steps for Maven jobs'() {
        setup:
        List<WithXmlAction> mockActions = Mock()
        StepContextHelper helper = new StepContextHelper(mockActions, JobType.Maven)

        when:
        helper.steps {}

        then:
        thrown(IllegalStateException)
    }

    def 'call sbt method minimal'() {
        when:
        context.sbt('SBT 0.12.3')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def sbtStep = context.stepNodes[0]
        sbtStep.name() == 'org.jvnet.hudson.plugins.SbtPluginBuilder'
        sbtStep.attribute('plugin') == 'sbt@1.4'
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
        sbtStep.attribute('plugin') == 'sbt@1.4'
        sbtStep.name[0].value() == 'SBT 0.12.3'
        sbtStep.jvmFlags[0].value() == ''
        sbtStep.sbtFlags[0].value() == ''
        sbtStep.actions[0].value() == 'test'
        sbtStep.subdirPath[0].value() == ''
    }
    def 'call sbt method full'() {
        when:
        context.sbt('SBT 0.12.3','test', '-Dsbt.log.noformat=true',  '-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -Dfile.encoding=UTF-8 -Xmx2G -Xms512M', 'subproject')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def sbtStep = context.stepNodes[0]
        sbtStep.name() == 'org.jvnet.hudson.plugins.SbtPluginBuilder'
        sbtStep.attribute('plugin') == 'sbt@1.4'
        sbtStep.name[0].value() == 'SBT 0.12.3'
        sbtStep.jvmFlags[0].value() == '-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -Dfile.encoding=UTF-8 -Xmx2G -Xms512M'
        sbtStep.sbtFlags[0].value() == '-Dsbt.log.noformat=true'
        sbtStep.actions[0].value() == 'test'
        sbtStep.subdirPath[0].value() == 'subproject'
    }

    def 'call dsl method defaults' () {
        when:
        context.dsl()

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
    }


    def 'call dsl method external script ignoring existing' () {
        when:
        context.dsl {
            removeAction 'DISABLE'
            external 'some-dsl.groovy','some-other-dsl.groovy'
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
            external 'some-dsl.groovy','some-other-dsl.groovy'
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
        context.dsl (['some-dsl.groovy','some-other-dsl.groovy','still-another-dsl.groovy'], 'DISABLE')

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
        context.dsl (['some-dsl.groovy','some-other-dsl.groovy','still-another-dsl.groovy'], 'DISABLE', true)

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

    def 'call downstream build step with all args'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2', 'UNSTABLE_OR_BETTER', true,
                    ["buildStepFailure": "FAILURE",
                            "failure": "FAILURE",
                            "unstable": "UNSTABLE"]) {
                currentBuild() // Current build parameters
                propertiesFile('dir/my.properties') // Parameters from properties file
                gitRevision(false) // Pass-through Git commit that was built
                predefinedProp('key1', 'value1') // Predefined properties
                predefinedProps([key2: 'value2', key3: 'value3'])
                predefinedProps('key4=value4\nkey5=value5') // Newline separated
                matrixSubset('label=="${TARGET}"') // Restrict matrix execution to a subset
                subversionRevision() // Subversion Revision
            }
            trigger('Project2') {
                currentBuild()
            }
        }

        then:
        Node stepNode = context.stepNodes[0]
        stepNode.name() == 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
        stepNode.configs[0].children().size() == 2
        Node first = stepNode.configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]
        first.projects[0].value() == 'Project1, Project2'
        first.condition[0].value() == 'UNSTABLE_OR_BETTER'
        first.triggerWithNoParameters[0].value() == 'true'
        first.configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
        first.configs[0].'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0].propertiesFile[0].value() == 'dir/my.properties'
        first.configs[0].'hudson.plugins.git.GitRevisionBuildParameters'[0].combineQueuedCommits[0].value() == 'false'
        first.configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'.size() == 1
        first.configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0].'properties'[0].value() ==
                'key1=value1\nkey2=value2\nkey3=value3\nkey4=value4\nkey5=value5'
        first.configs[0].'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0].filter[0].value() == 'label=="${TARGET}"'
        first.configs[0].'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0] instanceof Node
        first.block.size() == 1
        Node thresholds = first.block[0]
        thresholds.children().size() == 3
        Node unstableThreshold = thresholds.unstableThreshold[0]
        unstableThreshold.name[0].value() == 'UNSTABLE'
        Node failureThreshold = thresholds.failureThreshold[0]
        failureThreshold.name[0].value() == 'FAILURE'
        Node buildStepFailureThreshold = thresholds.buildStepFailureThreshold[0]
        buildStepFailureThreshold.name[0].value() == 'FAILURE'

        Node second = stepNode.configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[1]
        second.projects[0].value() == 'Project2'
        second.condition[0].value() == 'SUCCESS'
        second.triggerWithNoParameters[0].value() == 'false'
        second.configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
        second.block.isEmpty()

        when:
        context.downstreamParameterized {
            trigger('Project3') {
            }
        }

        then:
        Node third = context.stepNodes[1].configs[0].'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'[0]
        third.projects[0].value() == 'Project3'
        third.condition[0].value() == 'SUCCESS'
        third.triggerWithNoParameters[0].value() == 'false'
        third.configs[0].attribute('class') == 'java.util.Collections$EmptyList'

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
            runner("Fail")
            shell("look at me")
        }

        then:
        Node step = context.stepNodes[0]
        step.name() == 'org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder'
        step.condition[0].children().size() == testConditionArgs.values().size()

        Node condition = step.condition[0]
        def condClass
        if (testCondition == 'booleanCondition') {
            condClass = 'Boolean'
        } else {
            condClass = testCondition.capitalize()
        }

        condition.attribute('class') == "org.jenkins_ci.plugins.run_condition.core.${condClass}Condition"
        if (!testConditionArgs.isEmpty()) {
            testConditionArgs.each { k, v ->
                condition."${k}"[0].value() == "${v}"
            }
        }
        step.runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'

        Node childStep = step.buildStep[0]
        childStep.attribute('class') == 'hudson.tasks.Shell'
        childStep.command[0].value() == 'look at me'

        where:
        testCondition << ['stringsMatch', 'alwaysRun', 'neverRun', 'booleanCondition', 'cause', 'expression', 'time', 'status']
        testConditionArgs << [['arg1': 'foo', 'arg2': 'bar', 'ignoreCase': false], [:], [:],
                ['token': 'foo'], ['buildCause': 'foo', 'exclusiveCondition': true],
                ['expression': 'some-expression', 'label': 'some-label'],
                ['earliest': 'earliest-time', 'latest': 'latest-time', 'useBuildTime': false],
                ['worstResult': 'Success', 'bestResult': 'Success']]
    }

    @Unroll
    def 'call conditional steps for a single step with #runner'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            runner(runnerName)
            shell("look at me")
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
            runner("invalid-runner")
            shell("look at me")
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'call conditional steps with no condition'() {
        when:
        context.conditionalSteps {
            condition {
            }
            runner("Fail")
            shell("look at me")
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
            runner("Fail")
            shell("look at me")
        }

        then:
        thrown(MissingMethodException)
    }

    def 'call conditional steps for multiple steps'() {
        when:
        context.conditionalSteps {
            condition {
                stringsMatch("foo", "bar", false)
            }
            runner("Fail")
            shell("look at me")
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

        step.conditionalBuilders[0].children().size() == 2

        Node shellStep = step.conditionalBuilders[0].children()[0]
        shellStep.name() == 'hudson.tasks.Shell'
        shellStep.command[0].value() == 'look at me'

        def acmeGroovyNode = step.conditionalBuilders[0].children()[1]
        acmeGroovyNode.name() == 'hudson.plugins.groovy.Groovy'
        acmeGroovyNode.groovyName.size() == 1
        acmeGroovyNode.groovyName[0].value() == 'Groovy 2.0'
        acmeGroovyNode.parameters.size() == 1
        acmeGroovyNode.parameters[0].value() == "foo\nbar\nbaz"
        acmeGroovyNode.classPath.size() == 1
        acmeGroovyNode.classPath[0].value() == "/foo/acme.jar${File.pathSeparator}/foo/test.jar"
        acmeGroovyNode.scriptParameters.size() == 1
        acmeGroovyNode.scriptParameters[0].value() == "alfa\nbravo\ncharlie"
        acmeGroovyNode.properties.size() == 1
        acmeGroovyNode.properties[0].value() == "one=two\nthree=four\nfive=six"
        acmeGroovyNode.javaOpts.size() == 1
        acmeGroovyNode.javaOpts[0].value() == 'test me too'
        acmeGroovyNode.scriptSource.size() == 1
        def acmeScriptSourceNode = acmeGroovyNode.scriptSource[0]
        acmeScriptSourceNode.attribute('class') == 'hudson.plugins.groovy.StringScriptSource'
        acmeScriptSourceNode.command.size() == 1
        acmeScriptSourceNode.command[0].value() == 'acme.Acme.doSomething()'
    }
}
