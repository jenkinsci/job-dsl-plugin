package javaposse.jobdsl.dsl.helpers.triggers

import spock.lang.Specification

/**
 * UrlTriggerEntryContextSpec
 */
class UrlTriggerEntryContextSpec extends Specification {

    def 'missing URL causes NullPointerException' () {
        when:
        UrlTriggerEntryContext ctx = new UrlTriggerEntryContext(null)

        then:
        thrown(NullPointerException)
    }

    def 'empty URL causes IllegalArgumentException' () {
        when:
        UrlTriggerEntryContext ctx = new UrlTriggerEntryContext("")

        then:
        thrown(IllegalArgumentException)
    }

    def 'check default values'() {
        when:
        def ctx = new UrlTriggerEntryContext('http://www.example.com/')

        then:
        ctx.checks.empty
        ctx.statusCode == 200
        ctx.timeout == 300
        ctx.url == 'http://www.example.com/'
        !ctx.proxyActivated
        ctx.inspections.empty
    }

    def 'check invalid inspection causes exception'() {
        setup:
        def ctx = new UrlTriggerEntryContext('http://www.example.com/')

        when:
        ctx.inspection('foo')

        then:
        thrown(IllegalArgumentException)

    }

    def 'check invalid check causes exception'() {
        setup:
        def ctx = new UrlTriggerEntryContext('http://www.example.com/')

        when:
        ctx.check('foo')

        then:
        thrown(IllegalArgumentException)

    }
}
