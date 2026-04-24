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

    def 'dynamicBuildFilterColumn'() {
        when:
        view.columns {
            dynamicBuildFilterColumn('status')
        }

        then:
        Node root = view.node
        def cols = root.columns[0].children()
        cols.size() == 1
        cols[0].name() == 'io.jenkins.plugins.dynamic__view__filter.DynamicBuildFilterColumn'
        cols[0].delegate[0].@class == 'hudson.views.StatusColumn'
        1 * jobManagement.requirePlugin('dynamic-view-filter', true)
    }

    def 'dynamicBuildFilterColumn with all delegate types'() {
        when:
        view.columns {
            dynamicBuildFilterColumn(type)
        }

        then:
        Node root = view.node
        def col = root.columns[0].children()[0]
        col.name() == 'io.jenkins.plugins.dynamic__view__filter.DynamicBuildFilterColumn'
        col.delegate[0].@class == expected
        1 * jobManagement.requirePlugin('dynamic-view-filter', true)

        where:
        type           || expected
        'status'       || 'hudson.views.StatusColumn'
        'weather'      || 'hudson.views.WeatherColumn'
        'name'         || 'hudson.views.JobColumn'
        'lastSuccess'  || 'hudson.views.LastSuccessColumn'
        'lastFailure'  || 'hudson.views.LastFailureColumn'
        'lastDuration' || 'hudson.views.LastDurationColumn'
        'buildButton'  || 'hudson.views.BuildButtonColumn'
    }

    def 'dynamicBuildFilterColumn with null delegate'() {
        when:
        view.columns {
            dynamicBuildFilterColumn(null)
        }

        then:
        thrown(DslScriptException)
    }

    def 'dynamicBuildFilterColumn with unknown delegate'() {
        when:
        view.columns {
            dynamicBuildFilterColumn('unknown')
        }

        then:
        thrown(DslScriptException)
    }

    def 'parameterBuildFilterColumn'() {
        when:
        view.columns {
            parameterBuildFilterColumn('lastSuccess', 'region', 'east')
        }

        then:
        Node root = view.node
        def cols = root.columns[0].children()
        cols.size() == 1
        cols[0].name() == 'io.jenkins.plugins.dynamic__view__filter.ParameterBuildFilterColumn'
        cols[0].delegate[0].@class == 'hudson.views.LastSuccessColumn'
        cols[0].paramName.text() == 'region'
        cols[0].paramValueRegex.text() == 'east'
        1 * jobManagement.requirePlugin('dynamic-view-filter', true)
    }

    def 'parameterBuildFilterColumn with null paramName'() {
        when:
        view.columns {
            parameterBuildFilterColumn('status', null, 'east')
        }

        then:
        thrown(DslScriptException)
    }

    def 'parameterBuildFilterColumn with empty paramValueRegex'() {
        when:
        view.columns {
            parameterBuildFilterColumn('status', 'region', '')
        }

        then:
        thrown(DslScriptException)
    }

    def 'parameterRunMatcher with defaults'() {
        when:
        view.jobFilters {
            parameterRunMatcher {}
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1
        Node filter = filters[0]
        filter.name() == 'io.jenkins.plugins.dynamic__view__filter.ParameterRunMatcherFilter'
        filter.includeExcludeTypeString.text() == 'includeMatched'
        filter.nameRegex.text() == ''
        filter.valueRegex.text() == ''
        filter.descriptionRegex.text() == ''
        filter.useDefaultValue.text() == 'false'
        filter.matchAllBuilds.text() == 'true'
        filter.maxBuildsToMatch.text() == '0'
        filter.matchBuildsInProgress.text() == 'false'
        1 * jobManagement.requirePlugin('dynamic-view-filter', true)
    }

    def 'parameterRunMatcher with all properties'() {
        when:
        view.jobFilters {
            parameterRunMatcher {
                matchType('excludeMatched')
                nameRegex('.*region.*')
                valueRegex('us-east-1')
                descriptionRegex('deploy.*')
                useDefaultValue(true)
                matchAllBuilds(false)
                maxBuildsToMatch(5)
                matchBuildsInProgress(true)
            }
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1
        Node filter = filters[0]
        filter.name() == 'io.jenkins.plugins.dynamic__view__filter.ParameterRunMatcherFilter'
        filter.includeExcludeTypeString.text() == 'excludeMatched'
        filter.nameRegex.text() == '.*region.*'
        filter.valueRegex.text() == 'us-east-1'
        filter.descriptionRegex.text() == 'deploy.*'
        filter.useDefaultValue.text() == 'true'
        filter.matchAllBuilds.text() == 'false'
        filter.maxBuildsToMatch.text() == '5'
        filter.matchBuildsInProgress.text() == 'true'
        1 * jobManagement.requirePlugin('dynamic-view-filter', true)
    }

    def 'mixed columns with standard and dynamic-view-filter columns'() {
        when:
        view.columns {
            status()
            dynamicBuildFilterColumn('lastSuccess')
            parameterBuildFilterColumn('lastFailure', 'env', 'prod')
            name()
        }

        then:
        Node root = view.node
        def cols = root.columns[0].children()
        cols.size() == 4
        cols[0].name() == 'hudson.views.StatusColumn'
        cols[1].name() == 'io.jenkins.plugins.dynamic__view__filter.DynamicBuildFilterColumn'
        cols[2].name() == 'io.jenkins.plugins.dynamic__view__filter.ParameterBuildFilterColumn'
        cols[3].name() == 'hudson.views.JobColumn'
    }
}
