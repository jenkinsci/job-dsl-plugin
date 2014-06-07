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

    def 'compareTo'() {
        when:
        GeneratedView view = new GeneratedView('test')

        then:
        view.compareTo('test') == 0
        view.compareTo('test2') < 0
        view.compareTo('t') > 0
        view.compareTo(new GeneratedView('test')) == 0
        view.compareTo(new GeneratedView('test2')) < 0
        view.compareTo(new GeneratedView('t')) > 0
        view.compareTo(2) > 0
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
        view.toString() == "GeneratedJob{viewName='test'}"
    }
}
