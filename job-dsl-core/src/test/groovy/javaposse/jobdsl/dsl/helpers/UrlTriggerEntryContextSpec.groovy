package javaposse.jobdsl.dsl.helpers

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
}
