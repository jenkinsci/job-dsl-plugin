package javaposse.jobdsl.dsl

import spock.lang.Specification

class GeneratedViewSpec extends Specification {
    def 'name'() {
        when:
        def test = new TestView(new MemoryJobManagement()) {}.with { name = 'test'; it }
        GeneratedView view = new GeneratedView(test)

        then:
        view.name == 'test'
    }

    def 'null'() {
        when:
        new GeneratedView(null)

        then:
        thrown(IllegalArgumentException)
    }

    @SuppressWarnings(['ChangeToOperator', 'GrEqualsBetweenInconvertibleTypes'])
    def 'test equals'() {
        when:
        def test = new TestView(new MemoryJobManagement()) {}.with { name = 'test'; it }
        def foo = new TestView(new MemoryJobManagement()) {}.with { name = 'foo'; it }
        GeneratedView view = new GeneratedView(test)

        then:
        view.equals(view)
        view.equals(new GeneratedView(test))
        !view.equals(new GeneratedView(foo))
        !view.equals('test')
    }

    def 'test hashCode'() {
        when:
        def test = new TestView(new MemoryJobManagement()) {}.with { name = 'test'; it }
        def other = new TestView(new MemoryJobManagement()) {}.with { name = 'other'; it }
        GeneratedView view = new GeneratedView(test)

        then:
        view.hashCode() == new GeneratedView(test).hashCode()
        view.hashCode() != new GeneratedView(other).hashCode()
    }

    def 'test toString'() {
        when:
        def test = new TestView(new MemoryJobManagement()) {}.with { name = 'test'; it }
        GeneratedView view = new GeneratedView(test)

        then:
        view.toString() == "GeneratedView{name='test'}"
    }

    @SuppressWarnings('ChangeToOperator')
    def 'test compare'() {
        when:
        GeneratedView view1 = new GeneratedView('foo')
        GeneratedView view2 = new GeneratedView('other')

        then:
        view1.compareTo(view1) == 0
        view1.compareTo(view2) < 0
        view2.compareTo(view1) > 0
    }
}
