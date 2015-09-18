package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.jobfilter.RegexMatchValue
import javaposse.jobdsl.dsl.views.jobfilter.Status
import spock.lang.Specification

import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ALL
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.DISABLED
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ENABLED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.EXCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.EXCLUDE_UNMATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.INCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.INCLUDE_UNMATCHED
import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class ListViewSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    ListView view = new ListView(jobManagement)

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
        thrown(DslScriptException)
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
        thrown(DslScriptException)
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
        thrown(DslScriptException)

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
            claim()
            lastBuildNode()
            categorizedJob()
            cronTrigger()
            progressBar()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 12
        root.columns[0].value()[0].name() == 'hudson.views.StatusColumn'
        root.columns[0].value()[1].name() == 'hudson.views.WeatherColumn'
        root.columns[0].value()[2].name() == 'hudson.views.JobColumn'
        root.columns[0].value()[3].name() == 'hudson.views.LastSuccessColumn'
        root.columns[0].value()[4].name() == 'hudson.views.LastFailureColumn'
        root.columns[0].value()[5].name() == 'hudson.views.LastDurationColumn'
        root.columns[0].value()[6].name() == 'hudson.views.BuildButtonColumn'
        root.columns[0].value()[7].name() == 'hudson.plugins.claim.ClaimColumn'
        root.columns[0].value()[8].name() == 'org.jenkins.plugins.column.LastBuildNodeColumn'
        root.columns[0].value()[9].name() == 'org.jenkinsci.plugins.categorizedview.IndentedJobColumn'
        root.columns[0].value()[10].name() == 'hudson.plugins.CronViewColumn'
        root.columns[0].value()[11].name() == 'org.jenkins.ci.plugins.progress__bar.ProgressBarColumn'
        1 * jobManagement.requireMinimumPluginVersion('build-node-column', '0.1')
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '1.8')
        1 * jobManagement.requirePlugin('claim')
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
        1 * jobManagement.requirePlugin('extra-columns')
    }

    def 'configureProject column'() {
        when:
        view.columns {
            configureProject()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'jenkins.plugins.extracolumns.ConfigureProjectColumn'
        1 * jobManagement.requirePlugin('extra-columns')
    }

    def 'robotResults column'() {
        when:
        view.columns {
            robotResults()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'hudson.plugins.robot.view.RobotListViewColumn'
        1 * jobManagement.requireMinimumPluginVersion('robot', '1.6.0')
    }

    def 'statusFilter'(Closure filter, Map children) {
        when:
        view.jobFilters(filter)
        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node statusFilter = filters[0]
        statusFilter.name() == 'hudson.views.JobStatusFilter'
        statusFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert statusFilter.children()[idx].name() == name
            assert statusFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children
        { ->
            status {
                matchType(INCLUDE_UNMATCHED)
                status(Status.UNSTABLE, Status.STABLE, Status.ABORTED, Status.FAILED, Status.DISABLED)
            }
        } | [
                includeExcludeTypeString: 'includeUnmatched',
                unstable: true,
                failed: true,
                aborted: true,
                disabled: true,
                stable: true
        ]
        { ->
            status {
                matchType(INCLUDE_MATCHED)
            }
        } | [
                includeExcludeTypeString: 'includeMatched',
                unstable: false,
                failed: false,
                aborted: false,
                disabled: false,
                stable: false
        ]
        { ->
            status {
                matchType(EXCLUDE_MATCHED)
                status(Status.UNSTABLE)
            }
        } | [
                includeExcludeTypeString: 'excludeMatched',
                unstable: true,
                failed: false,
                aborted: false,
                disabled: false,
                stable: false
        ]
        { ->
            status {
                matchType(EXCLUDE_UNMATCHED)
                status(Status.ABORTED, Status.DISABLED)
            }
        } | [
                includeExcludeTypeString: 'excludeUnmatched',
                unstable: false,
                failed: false,
                aborted: true,
                disabled: true,
                stable: false
        ]
    }

    def 'regex job filter'(RegexMatchValue regexType, String regexString) {
        when:
        view.jobFilters {
            regex {
                matchType(INCLUDE_UNMATCHED)
                matchValue regexType
                regex regexString
            }
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node statusFilter = filters[0]
        statusFilter.name() == 'hudson.views.RegExJobFilter'
        statusFilter.children().size() == 3
        statusFilter.children()[0].name() == 'includeExcludeTypeString'
        statusFilter.children()[0].value() == 'includeUnmatched'
        statusFilter.children()[1].name() == 'valueTypeString'
        statusFilter.children()[1].value() == regexType.name()
        statusFilter.children()[2].name() == 'regex'
        statusFilter.children()[2].value() == regexString
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        regexType           || regexString
        RegexMatchValue.NAME | '.*'
    }

    def 'recurse folders'() {
        when:
        view.recurse()

        then:
        Node root = view.node
        root.recurse[0].text() == 'true'

    }

    def 'customIcon column'() {
        when:
        view.columns {
            customIcon()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'jenkins.plugins.jobicon.CustomIconColumn'
        1 * jobManagement.requireMinimumPluginVersion('custom-job-icon', '0.2')
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
