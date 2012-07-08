package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.StepHelper.StepContext
import spock.lang.Specification

public class StepHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    StepHelper helper = new StepHelper(mockActions)
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
