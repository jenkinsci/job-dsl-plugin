package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.jobfilter.AmountType
import javaposse.jobdsl.dsl.views.jobfilter.BuildCountType
import javaposse.jobdsl.dsl.views.jobfilter.BuildStatusType
import javaposse.jobdsl.dsl.views.jobfilter.RegexMatchValue
import javaposse.jobdsl.dsl.views.jobfilter.Status
import spock.lang.Specification
import spock.lang.Unroll

import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ALL
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.DISABLED
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ENABLED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.EXCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.EXCLUDE_UNMATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.INCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.INCLUDE_UNMATCHED
import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class ListViewSpec<T extends ListView> extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    T view = new ListView(jobManagement, 'test')

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

    def 'add several columns'() {
        when:
        view.columns {
            status()
            weather()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 6
        root.columns[0].value()[0].name() == 'hudson.views.StatusColumn'
        root.columns[0].value()[1].name() == 'hudson.views.WeatherColumn'
        root.columns[0].value()[2].name() == 'hudson.views.LastSuccessColumn'
        root.columns[0].value()[3].name() == 'hudson.views.LastFailureColumn'
        root.columns[0].value()[4].name() == 'hudson.views.LastDurationColumn'
        root.columns[0].value()[5].name() == 'hudson.views.BuildButtonColumn'
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

    def 'most recent jobs filter'(Closure filter, Map children) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node mostRecentJobsFilter = filters[0]
        mostRecentJobsFilter.name() == 'hudson.views.MostRecentJobsFilter'
        mostRecentJobsFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert mostRecentJobsFilter.children()[idx].name() == name
            assert mostRecentJobsFilter.children()[idx].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('view-job-filters', '1.27')

        where:
        filter || children
                { ->
                    mostRecent()
                } | [
                maxToInclude  : 0,
                checkStartTime: false
        ]
                { ->
                    mostRecent {
                        maxToInclude(5)
                        checkStartTime()
                    }
                } | [
                maxToInclude  : 5,
                checkStartTime: true
        ]
    }

    def 'unclassified jobs filter'(Closure filter, Map children) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node unclassifiedJobsFilter = filters[0]
        unclassifiedJobsFilter.name() == 'hudson.views.UnclassifiedJobsFilter'
        unclassifiedJobsFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert unclassifiedJobsFilter.children()[idx].name() == name
            assert unclassifiedJobsFilter.children()[idx].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('view-job-filters', '1.27')

        where:
        filter || children
                { ->
                    unclassified()
                } | [
                includeExcludeTypeString: 'includeMatched'
        ]
                { ->
                    unclassified {
                        matchType(INCLUDE_UNMATCHED)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched'
        ]
    }

    def 'build trend filter'(Closure filter, Map children) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node buildTrendFilter = filters[0]
        buildTrendFilter.name() == 'hudson.views.BuildTrendFilter'
        buildTrendFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert buildTrendFilter.children()[idx].name() == name
            assert buildTrendFilter.children()[idx].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('view-job-filters', '1.27')

        where:
        filter || children
                { ->
                    buildTrend()
                } | [
                includeExcludeTypeString: 'includeMatched',
                buildCountTypeString    : 'Latest',
                amountTypeString        : 'Hours',
                amount                  : 0.0,
                statusTypeString        : 'Completed'
        ]
                { ->
                    buildTrend {
                        matchType(INCLUDE_UNMATCHED)
                        buildCountType(BuildCountType.AT_LEAST_ONE)
                        amountType(AmountType.DAYS)
                        amount(2.5)
                        status(BuildStatusType.TRIGGERED_BY_REMOTE)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                buildCountTypeString    : 'AtLeastOne',
                amountTypeString        : 'Days',
                amount                  : 2.5,
                statusTypeString        : 'TriggeredByRemote'
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

    def 'all jobs filter'() {
        when:
        view.jobFilters {
            all()
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node allJobsFilter = filters[0]
        allJobsFilter.name() == 'hudson.views.AllJobsFilter'
        allJobsFilter.children().size() == 0

        jobManagement.requireMinimumPluginVersion('view-job-filters', '1.27')
    }

    def 'all release jobs filter'() {
        when:
        view.jobFilters {
            allRelease()
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node allJobsFilter = filters[0]
        allJobsFilter.name() == 'hudson.plugins.release.AllReleaseJobsFilter'
        allJobsFilter.children().size() == 0

        1 * jobManagement.requireMinimumPluginVersion('release', '2.5.3')
    }

    def 'release jobs filter'() {
        when:
        view.jobFilters {
            release()
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node releaseJobsFilter = filters[0]
        releaseJobsFilter.name() == 'hudson.plugins.release.ReleaseJobsFilter'
        releaseJobsFilter.children().size() == 0

        1 * jobManagement.requireMinimumPluginVersion('release', '2.5.3')
    }

    def 'job filter extension'() {
        setup:
        jobManagement.callExtension('extension', null, JobFiltersContext) >> new Node(null, 'foo')

        when:
        view.jobFilters {
            extension()
        }

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1
        with(filters[0]) {
            name() == 'foo'
            children().size() == 0
        }
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

    def 'release button column'() {
        when:
        view.columns {
            releaseButton()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'hudson.plugins.release.ReleaseButtonColumn'
        1 * jobManagement.requireMinimumPluginVersion('release', '2.3')
    }

    def 'jacoco column'() {
        when:
        view.columns {
            jacoco()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'hudson.plugins.jacococoveragecolumn.JaCoCoColumn'
        1 * jobManagement.requireMinimumPluginVersion('jacoco', '1.0.10')
    }

    @Unroll
    def '#type column'() {
        when:
        view.columns {
            "$type"()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == "jenkins.plugins.extracolumns.$xmlType"
        1 * jobManagement.requireMinimumPluginVersion('extra-columns', minimumPluginVersion)

        where:
        type                            | xmlType                                  | minimumPluginVersion
        'lastBuildNode'                 | 'LastBuildNodeColumn'                    | '1.16'
        'slaveOrLabel'                  | 'SlaveOrLabelColumn'                     | '1.14'
        'userName'                      | 'UserNameColumn'                         | '1.16'
        'lastConfigurationModification' | 'LastJobConfigurationModificationColumn' | '1.14'
        'workspace'                     | 'WorkspaceColumn'                        | '1.15'
        'scmType'                       | 'SCMTypeColumn'                          | '1.4'
    }

    def 'build parameters column'() {
        when:
        view.columns {
            buildParameters()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        def columns = root.columns[0].value()
        columns.size() == 1

        Node column = columns[0]
        column.name() == 'jenkins.plugins.extracolumns.BuildParametersColumn'
        column.children().size() == 2
        column.singlePara[0].value() == false
        column.parameterName[0].value() == ''
        1 * jobManagement.requireMinimumPluginVersion('extra-columns', '1.13')
    }

    def 'build parameters column for a single parameter'() {
        when:
        view.columns {
            buildParameters('PARAM')
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        def columns = root.columns[0].value()
        columns.size() == 1

        Node column = columns[0]
        column.name() == 'jenkins.plugins.extracolumns.BuildParametersColumn'
        column.children().size() == 2
        column.singlePara[0].value() == true
        column.parameterName[0].value() == 'PARAM'
        1 * jobManagement.requireMinimumPluginVersion('extra-columns', '1.13')
    }

    def 'disable project column with button'() {
        when:
        view.columns {
            disableProject()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        def columns = root.columns[0].value()
        columns.size() == 1

        Node column = columns[0]
        column.name() == 'jenkins.plugins.extracolumns.DisableProjectColumn'
        column.children().size() == 1
        column.useIcon[0].value() == false
        1 * jobManagement.requireMinimumPluginVersion('extra-columns', '1.7')
    }

    def 'disable project column with icon'() {
        when:
        view.columns {
            disableProject(true)
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        def columns = root.columns[0].value()
        columns.size() == 1

        Node column = columns[0]
        column.name() == 'jenkins.plugins.extracolumns.DisableProjectColumn'
        column.children().size() == 1
        column.useIcon[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('extra-columns', '1.7')
    }

    def 'test result column'() {
        when:
        view.columns {
            testResult(2)
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        def columns = root.columns[0].value()
        columns.size() == 1

        Node column = columns[0]
        column.name() == 'jenkins.plugins.extracolumns.TestResultColumn'
        column.children().size() == 1
        column.testResultFormat[0].value() == 2
        1 * jobManagement.requireMinimumPluginVersion('extra-columns', '1.6')
    }

    def 'next launch column'() {
        when:
        view.columns {
            nextLaunch()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'hudson.plugins.nextexecutions.columns.NextExecutionColumn'
        root.columns[0].value().triggerClass[0].value() == 'hudson.triggers.TimerTrigger'
        1 * jobManagement.requireMinimumPluginVersion('next-executions', '1.0.12')
    }

    def 'next possible launch column'() {
        when:
        view.columns {
            nextPossibleLaunch()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'hudson.plugins.nextexecutions.columns.PossibleNextExecutionColumn'
        root.columns[0].value().triggerClass[0].value() == 'hudson.triggers.SCMTrigger'
        1 * jobManagement.requireMinimumPluginVersion('next-executions', '1.0.12')
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
