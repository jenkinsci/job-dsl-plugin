package javaposse.jobdsl.dsl

import spock.lang.Specification

class DslScriptExceptionSpec extends Specification {
    def 'constructor with message'() {
        when:
        Exception e = new DslScriptException('foo')

        then:
        e.message =~ /\(.+, line \d+\) foo/
    }

    def 'constructor with message and cause'() {
        setup:
        Throwable cause = new Exception()

        when:
        Exception e = new DslScriptException('foo', cause)

        then:
        e.message =~ /\(.+, line \d+\) foo/
        e.cause == cause
    }
}
