package javaposse.jobdsl.dsl

import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class ViewSpec extends Specification {
    View view = Spy(View)

    def 'name'() {
        when:
        view.name('test')

        then:
        view.name == 'test'
    }

    def 'description'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        view.description('test view')

        then:
        Node root = view.getNode()
        view.executeWithXmlActions(root)
        root.description.size() == 1
        root.description[0].text() == 'test view'
    }

    def 'filterBuildQueue'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        view.filterBuildQueue(true)

        then:
        Node root = view.getNode()
        view.executeWithXmlActions(root)
        root.filterQueue.size() == 1
        root.filterQueue[0].text() == 'true'
    }

    def 'filterBuildQueue without argument'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        view.filterBuildQueue()

        then:
        Node root = view.getNode()
        view.executeWithXmlActions(root)
        root.filterQueue.size() == 1
        root.filterQueue[0].text() == 'true'
    }

    def 'filterExecutors'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        view.filterExecutors(true)

        then:
        Node root = view.getNode()
        view.executeWithXmlActions(root)
        root.filterExecutors.size() == 1
        root.filterExecutors[0].text() == 'true'
    }

    def 'filterExecutors without argument'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        view.filterExecutors()

        then:
        Node root = view.getNode()
        view.executeWithXmlActions(root)
        root.filterExecutors.size() == 1
        root.filterExecutors[0].text() == 'true'
    }

    def 'configure'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        view.configure {
            it / foo('bar')
        }

        then:
        Node root = view.getNode()
        view.executeWithXmlActions(root)
        root.foo.size() == 1
        root.foo[0].text() == 'bar'
    }

    def 'xml'() {
        setup:
        view.getTemplate() >> "<View/>"

        when:
        String xml = view.getXml()

        then:
        compareXML("<View/>", xml).similar()
    }
}
