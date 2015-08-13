package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.DslScriptException
import spock.lang.Specification

class DownstreamTriggerParameterFactoryContextSpec extends Specification {
    DownstreamTriggerParameterFactoryContext context = new DownstreamTriggerParameterFactoryContext()

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
