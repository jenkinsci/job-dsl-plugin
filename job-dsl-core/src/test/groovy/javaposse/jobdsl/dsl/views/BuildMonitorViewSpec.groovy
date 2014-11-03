package javaposse.jobdsl.dsl.views

import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class BuildMonitorViewSpec extends Specification {
    BuildMonitorView view = new BuildMonitorView()

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        setIgnoreWhitespace(true)
        compareXML(defaultXml, xml).similar()
    }

    def 'add job by name'() {
        when:
        view.jobs {
            name('foo')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 1
        root.jobNames[0].string[0].text() == 'foo'
    }

    def 'job name null'() {
        when:
        view.jobs {
            name(null)
        }

        then:
        thrown(NullPointerException)
    }

    def 'add jobs by name'() {
        when:
        view.jobs {
            names('foo', 'bar')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 2
        root.jobNames[0].string[0].text() == 'bar'
        root.jobNames[0].string[1].text() == 'foo'
    }

    def 'job names null'() {
        when:
        view.jobs {
            names('foo', null)
        }

        then:
        thrown(NullPointerException)

    }

    def 'add jobs by regex'() {
        when:
        view.jobs {
            regex('test')
        }

        then:
        Node root = view.node
        root.includeRegex.size() == 1
        root.includeRegex[0].text() == 'test'
    }

    def 'call jobs twice'() {
        when:
        view.jobs {
            name('foo')
        }
        view.jobs {
            name('bar')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 2
        root.jobNames[0].string[0].text() == 'bar'
        root.jobNames[0].string[1].text() == 'foo'
    }

    def 'call jobs complex'() {
        when:
        view.jobs {
            name('foo')
            names('bar', 'other')
            regex('test')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 3
        root.jobNames[0].string[0].text() == 'bar'
        root.jobNames[0].string[1].text() == 'foo'
        root.jobNames[0].string[2].text() == 'other'
        root.includeRegex.size() == 1
        root.includeRegex[0].text() == 'test'
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView>
    <owner class="hudson" reference="../../.."/>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames/>
    <jobFilters/>
    <columns/>
    <recurse>false</recurse>
    <order class="com.smartcodeltd.jenkinsci.plugins.buildmonitor.order.ByName"/>
</com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView>'''
}
