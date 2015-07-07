package javaposse.jobdsl.dsl

import spock.lang.Specification

class DslExceptionSpec extends Specification {
    def 'constructor with message'() {
        when:
        Exception e = new DslException('foo')

        then:
        e.message == 'foo'
    }

    def 'constructor with message and cause'() {
        setup:
        Throwable cause = new Exception()

        when:
        Exception e = new DslException('foo', cause)

        then:
        e.message == 'foo'
        e.cause == cause
    }
}
