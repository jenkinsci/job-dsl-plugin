package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.StepContextHelper.StepContext
import spock.lang.Specification

public class StepHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    StepContextHelper helper = new StepContextHelper(mockActions)
    StepContext context = new StepContext()

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

    def 'call maven methods'() {
        when:
        context.maven('install')

        then:
        context.stepNodes != null
        context.stepNodes.size() == 1
        def mavenStep = context.stepNodes[0]
        mavenStep.name() == 'hudson.tasks.Maven'
        mavenStep.targets[0].value() == 'install'
        mavenStep.pom[0].value() == ''

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

    def 'call minimal copyArtifacts'() {
        when: 'Least arguments'
        context.copyArtifacts('upstream', '**/*.xml') {
            upstream()
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
            upstream()
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
        selectorNode.children().size() == 0
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
        def withXmlAction = helper.generateWithXmlAction(new StepContext([stepNode]))
        withXmlAction.execute(root)

        then:
        root.builders[0].'hudson.tasks.Shell'[0].command[0].text() == 'ls'
    }
}
