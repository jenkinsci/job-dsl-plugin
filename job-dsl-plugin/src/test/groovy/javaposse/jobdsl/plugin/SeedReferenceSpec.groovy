package javaposse.jobdsl.plugin

import spock.lang.Specification

class SeedReferenceSpec extends Specification {
    def 'constructor with one argument'() {
        when:
        SeedReference seedReference = new SeedReference('foo')

        then:
        seedReference.seedJobName == 'foo'
        seedReference.templateJobName == null
        seedReference.digest == null
    }

    def 'constructor with three arguments'() {
        when:
        SeedReference seedReference = new SeedReference('foo', 'bar', 'baz')

        then:
        seedReference.seedJobName == 'bar'
        seedReference.templateJobName == 'foo'
        seedReference.digest == 'baz'
    }

    def 'templateJobName is mutable'() {
        when:
        SeedReference seedReference = new SeedReference('foo', 'bar', 'baz')
        seedReference.templateJobName = 'test'

        then:
        seedReference.seedJobName == 'bar'
        seedReference.templateJobName == 'test'
        seedReference.digest == 'baz'
    }

    def 'digest is mutable'() {
        when:
        SeedReference seedReference = new SeedReference('foo', 'bar', 'baz')
        seedReference.digest = 'test'

        then:
        seedReference.seedJobName == 'bar'
        seedReference.templateJobName == 'foo'
        seedReference.digest == 'test'
    }

    def 'equals works as expected'() {
        when:
        SeedReference seedReference = new SeedReference('foo', 'bar', 'baz')

        then:
        seedReference == seedReference
        seedReference == new SeedReference('foo', 'bar', 'baz')
        seedReference != 34
        seedReference != new SeedReference('foo')
        seedReference != new SeedReference('foo', 'bar', null)
        seedReference != new SeedReference(null, null, null)
        new SeedReference('foo') != seedReference
        new SeedReference('foo', 'bar', null) != seedReference
        new SeedReference(null, null, null) != seedReference
        new SeedReference('test', 'bar', 'baz') != seedReference
    }
}
