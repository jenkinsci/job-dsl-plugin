package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class DropdownFilterViewSpec extends Specification {
    private static final String DEFAULT_XML = '''<?xml version="1.1" encoding="UTF-8"?>
        <io.jenkins.plugins.dynamic__view__filter.DropdownFilterView>
            <filterExecutors>false</filterExecutors>
            <filterQueue>false</filterQueue>
            <properties class="hudson.model.View$PropertyList"/>
            <jobNames class="tree-set">
                <comparator class="hudson.util.CaseInsensitiveComparator"/>
            </jobNames>
            <jobFilters/>
            <columns/>
            <recurse>false</recurse>
            <filterPosition>top</filterPosition>
            <dropdowns/>
        </io.jenkins.plugins.dynamic__view__filter.DropdownFilterView>'''

    private final JobManagement jobManagement = Mock(JobManagement)
    private final DropdownFilterView view = new DropdownFilterView(jobManagement, 'test')

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'defaults'() {
        expect:
        compareXML(DEFAULT_XML, view.xml).similar()
    }

    def 'filterPosition sidebar'() {
        when:
        view.filterPosition('sidebar')

        then:
        Node root = view.node
        root.filterPosition.text() == 'sidebar'
    }

    def 'filterPosition null'() {
        when:
        view.filterPosition(null)

        then:
        thrown(DslScriptException)
    }

    def 'dropdowns with jobNameRegex'() {
        when:
        view.dropdowns {
            jobNameRegex('Project', 'projects/([^/]+)/.*')
        }

        then:
        Node root = view.node
        root.dropdowns.'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition'.size() == 1
        with(root.dropdowns.'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition'[0]) {
            label.text() == 'Project'
            sourceType.text() == 'jobNameRegex'
            jobNamePattern.text() == 'projects/([^/]+)/.*'
            parameterName.text() == ''
        }
    }

    def 'dropdowns with buildParameter'() {
        when:
        view.dropdowns {
            buildParameter('Environment', 'env')
        }

        then:
        Node root = view.node
        root.dropdowns.'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition'.size() == 1
        with(root.dropdowns.'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition'[0]) {
            label.text() == 'Environment'
            sourceType.text() == 'buildParameter'
            jobNamePattern.text() == ''
            parameterName.text() == 'env'
        }
    }

    def 'dropdowns with multiple entries'() {
        when:
        view.dropdowns {
            jobNameRegex('Team', 'teams/([^/]+)/.*')
            buildParameter('Region', 'region')
        }

        then:
        Node root = view.node
        root.dropdowns.'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition'.size() == 2
    }

    def 'jobNameRegex with empty label'() {
        when:
        view.dropdowns {
            jobNameRegex('', 'some/pattern')
        }

        then:
        thrown(DslScriptException)
    }

    def 'jobNameRegex with null label'() {
        when:
        view.dropdowns {
            jobNameRegex(null, 'some/pattern')
        }

        then:
        thrown(DslScriptException)
    }

    def 'buildParameter with empty parameterName'() {
        when:
        view.dropdowns {
            buildParameter('Label', '')
        }

        then:
        thrown(DslScriptException)
    }

    def 'buildParameter with null parameterName'() {
        when:
        view.dropdowns {
            buildParameter('Label', null)
        }

        then:
        thrown(DslScriptException)
    }

    def 'recurse inherited from ListView'() {
        when:
        view.recurse()

        then:
        Node root = view.node
        root.recurse.text() == 'true'
    }

    def 'jobs with regex inherited from ListView'() {
        when:
        view.jobs {
            regex('project-.*')
        }

        then:
        Node root = view.node
        root.includeRegex.text() == 'project-.*'
    }

    def 'columns inherited from ListView'() {
        when:
        view.columns {
            status()
            name()
        }

        then:
        Node root = view.node
        root.columns[0].children().size() == 2
        root.columns[0].'hudson.views.StatusColumn'.size() == 1
        root.columns[0].'hudson.views.JobColumn'.size() == 1
    }
}
