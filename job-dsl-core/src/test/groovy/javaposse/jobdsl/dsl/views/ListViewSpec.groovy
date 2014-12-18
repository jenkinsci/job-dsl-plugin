package javaposse.jobdsl.dsl.views

import spock.lang.Specification

import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ALL
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.DISABLED
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ENABLED
import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class ListViewSpec extends Specification {
    ListView view = new ListView()

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        setIgnoreWhitespace(true)
        compareXML(defaultXml, xml).similar()
    }

    def 'statusFilter ALL'() {
        when:
        view.statusFilter(ALL)

        then:
        Node root = view.node
        root.statusFilter.size() == 0
    }

    def 'statusFilter ALL remove previous statusFilter'() {
        when:
        view.statusFilter(ENABLED)
        view.statusFilter(ALL)

        then:
        Node root = view.node
        root.statusFilter.size() == 0
    }

    def 'statusFilter ENABLED'() {
        when:
        view.statusFilter(ENABLED)

        then:
        Node root = view.node
        root.statusFilter.size() == 1
        root.statusFilter[0].text() == 'true'
    }

    def 'statusFilter DISABLED'() {
        when:
        view.statusFilter(DISABLED)

        then:
        Node root = view.node
        root.statusFilter.size() == 1
        root.statusFilter[0].text() == 'false'
    }

    def 'statusFilter creates only one node'() {
        when:
        view.statusFilter(DISABLED)
        view.statusFilter(ENABLED)
        view.statusFilter(DISABLED)

        then:
        Node root = view.node
        root.statusFilter.size() == 1
        root.statusFilter[0].text() == 'false'
    }

    def 'statusFilter null'() {
        when:
        view.statusFilter(null)

        then:
        thrown(NullPointerException)
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

    def 'empty columns'() {
        when:
        view.columns {
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].children.size() == 0
    }

    def 'add all columns'() {
        when:
        view.columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 7
        root.columns[0].value()[0].name() == 'hudson.views.StatusColumn'
        root.columns[0].value()[1].name() == 'hudson.views.WeatherColumn'
        root.columns[0].value()[2].name() == 'hudson.views.JobColumn'
        root.columns[0].value()[3].name() == 'hudson.views.LastSuccessColumn'
        root.columns[0].value()[4].name() == 'hudson.views.LastFailureColumn'
        root.columns[0].value()[5].name() == 'hudson.views.LastDurationColumn'
        root.columns[0].value()[6].name() == 'hudson.views.BuildButtonColumn'
    }

    def 'call columns twice'() {
        when:
        view.columns {
            name()
        }
        view.columns {
            buildButton()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 2
        root.columns[0].value()[0].name() == 'hudson.views.JobColumn'
        root.columns[0].value()[1].name() == 'hudson.views.BuildButtonColumn'
    }

    def 'lastBuildConsole column'() {
        when:
        view.columns {
            lastBuildConsole()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'jenkins.plugins.extracolumns.LastBuildConsoleColumn'
    }

    protected String getDefaultXml() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.model.ListView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames class="tree-set">
        <comparator class="hudson.util.CaseInsensitiveComparator"/>
    </jobNames>
    <jobFilters/>
    <columns/>
</hudson.model.ListView>'''
    }
}
