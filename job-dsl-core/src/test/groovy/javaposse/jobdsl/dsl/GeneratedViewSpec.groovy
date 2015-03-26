package javaposse.jobdsl.dsl

import spock.lang.Specification

class GeneratedViewSpec extends Specification {
    def 'name'() {
        when:
        GeneratedView view = new GeneratedView('test')

        then:
        view.name == 'test'
    }

    def 'null'() {
        when:
        new GeneratedView(null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'test equals'() {
        when:
        GeneratedView view = new GeneratedView('test')

        then:
        view.equals(view)
        view.equals(new GeneratedView('test'))
        !view.equals(new GeneratedView('foo'))
        !view.equals('test')
    }

    def 'test hashCode'() {
        when:
        GeneratedView view = new GeneratedView('test')

        then:
        view.hashCode() == new GeneratedView('test').hashCode()
        view.hashCode() != new GeneratedView('other').hashCode()
    }

    def 'test toString'() {
        when:
        GeneratedView view = new GeneratedView('test')

        then:
        view.toString() == "GeneratedView{name='test'}"
    }
}
