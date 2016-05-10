package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class DownstreamTriggerParameterFactoryContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    DownstreamTriggerParameterFactoryContext context = new DownstreamTriggerParameterFactoryContext(jobManagement, item)

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.configFactories[0] == node
    }

    def 'forMatchingFiles with minimum options'() {
        when:
        context.forMatchingFiles('foo', 'bar')

        then:
        context.configFactories.size() == 1
        with(context.configFactories[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.BinaryFileParameterFactory'
            children().size() == 3
            parameterName.text() == 'bar'
            filePattern.text() == 'foo'
            noFilesFoundAction.text() == 'SKIP'
        }
    }

    def 'forMatchingFiles with all options'() {
        when:
        context.forMatchingFiles('foo', 'bar', action)

        then:
        context.configFactories.size() == 1
        with(context.configFactories[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.BinaryFileParameterFactory'
            children().size() == 3
            parameterName.text() == 'bar'
            filePattern.text() == 'foo'
            noFilesFoundAction.text() == action
        }

        where:
        action << ['SKIP', 'NOPARMS', 'FAIL']
    }

    def 'forMatchingFiles with invalid action'() {
        when:
        context.forMatchingFiles('foo', 'bar', action)

        then:
        thrown(DslScriptException)

        where:
        action << [null, '', 'FOO']
    }
}
