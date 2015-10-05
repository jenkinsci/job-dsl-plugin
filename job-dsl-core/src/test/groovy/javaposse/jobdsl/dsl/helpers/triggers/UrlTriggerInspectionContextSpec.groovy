package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.DslScriptException
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.triggers.UrlTriggerInspectionContext.Inspection.json
import static javaposse.jobdsl.dsl.helpers.triggers.UrlTriggerInspectionContext.Inspection.text

class UrlTriggerInspectionContextSpec extends Specification {
    def 'missing URL causes NullPointerException'() {
        when:
        new UrlTriggerInspectionContext(null)

        then:
        thrown(DslScriptException)
    }

    def 'ensure that paths are not null'() {
        setup:
        def ctx = new UrlTriggerInspectionContext(json)

        when:
        ctx.path(null)

        then:
        thrown(DslScriptException)
    }

    def 'ensure that paths are not empty'() {
        setup:
        def ctx = new UrlTriggerInspectionContext(json)

        when:
        ctx.path('')

        then:
        thrown(DslScriptException)
    }

    def 'ensure that RegExps are not null'() {
        setup:
        def ctx = new UrlTriggerInspectionContext(text)

        when:
        ctx.regexp(null)

        then:
        thrown(DslScriptException)
    }

    def 'ensure that RegExps are not empty'() {
        setup:
        def ctx = new UrlTriggerInspectionContext(text)

        when:
        ctx.regexp('')

        then:
        thrown(DslScriptException)
    }

    def 'ensure that RegExps are compileable'() {
        setup:
        def ctx = new UrlTriggerInspectionContext(text)

        when:
        ctx.regexp('(foo.+')

        then:
        thrown(DslScriptException)
    }
}
