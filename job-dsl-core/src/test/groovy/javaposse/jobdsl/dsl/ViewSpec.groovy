package javaposse.jobdsl.dsl

import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class ViewSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    View view = new TestView(jobManagement, 'test')

    def 'description'() {
        when:
        view.description('test view')

        then:
        Node root = view.node
        root.description.size() == 1
        root.description[0].text() == 'test view'
    }

    def 'filterBuildQueue'() {
        when:
        view.filterBuildQueue(true)

        then:
        Node root = view.node
        root.filterQueue.size() == 1
        root.filterQueue[0].text() == 'true'
    }

    def 'filterBuildQueue without argument'() {
        when:
        view.filterBuildQueue()

        then:
        Node root = view.node
        root.filterQueue.size() == 1
        root.filterQueue[0].text() == 'true'
    }

    def 'filterExecutors'() {
        when:
        view.filterExecutors(true)

        then:
        Node root = view.node
        root.filterExecutors.size() == 1
        root.filterExecutors[0].text() == 'true'
    }

    def 'filterExecutors without argument'() {
        when:
        view.filterExecutors()

        then:
        Node root = view.node
        root.filterExecutors.size() == 1
        root.filterExecutors[0].text() == 'true'
    }

    def 'configure'() {
        when:
        view.configure {
            it / foo('bar')
        }

        then:
        Node root = view.node
        root.foo.size() == 1
        root.foo[0].text() == 'bar'
    }

    def 'xml'() {
        when:
        String xml = view.xml

        then:
        compareXML('<View/>', xml).similar()
    }
}
