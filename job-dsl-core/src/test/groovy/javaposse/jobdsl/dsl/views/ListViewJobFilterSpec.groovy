package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.jobfilter.AmountType
import javaposse.jobdsl.dsl.views.jobfilter.BuildCountType
import javaposse.jobdsl.dsl.views.jobfilter.BuildStatusType
import javaposse.jobdsl.dsl.views.jobfilter.FallbackType
import javaposse.jobdsl.dsl.views.jobfilter.JobType
import javaposse.jobdsl.dsl.views.jobfilter.PermissionCheckType
import javaposse.jobdsl.dsl.views.jobfilter.ScmType
import spock.lang.Specification

import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.EXCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.EXCLUDE_UNMATCHED
import static javaposse.jobdsl.dsl.views.jobfilter.MatchType.INCLUDE_UNMATCHED

class ListViewJobFilterSpec<T extends ListView> extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    T view = new ListView(jobManagement)

    def 'allJobsFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node allJobsFilter = filters[0]
        allJobsFilter.name() == 'hudson.views.AllJobsFilter'
        allJobsFilter.children().size() == 0

        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    all()
                } | [:] | ''
    }

    def 'allReleaseJobsFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node allJobsFilter = filters[0]
        allJobsFilter.name() == 'hudson.views.AllReleaseJobsFilter'
        allJobsFilter.children().size() == 0

        1 * jobManagement.requirePlugin('release')

        where:
        filter || children || noop
                { ->
                    allRelease()
                } | [:] | ''
    }

    def 'buildDurationFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node buildDurationFilter = filters[0]
        buildDurationFilter.name() == 'hudson.views.BuildDurationFilter'
        buildDurationFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert buildDurationFilter.children()[idx].name() == name
            assert buildDurationFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    buildDuration()
                } | [
                includeExcludeTypeString: 'includeMatched',
                buildCountTypeString    : 'Latest',
                amountTypeString        : 'Hours',
                amount                  : 0.0,
                lessThan                : false,
                buildDurationMinutes    : 0.0
        ] | ''
                { ->
                    buildDuration {
                        matchType(INCLUDE_UNMATCHED)
                        buildCountType(BuildCountType.AT_LEAST_ONE)
                        amountType(AmountType.DAYS)
                        amount(1.5)
                        lessThan()
                        buildDuration(6.2)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                buildCountTypeString    : 'AtLeastOne',
                amountTypeString        : 'Days',
                amount                  : 1.5,
                lessThan                : true,
                buildDurationMinutes    : 6.2
        ] | ''
                { ->
                    buildDuration {
                        matchType(EXCLUDE_MATCHED)
                        buildCountType(BuildCountType.ALL)
                        amountType(AmountType.BUILDS)
                        amount(5)
                        lessThan()
                        buildDuration(20)
                    }
                } | [
                includeExcludeTypeString: 'excludeMatched',
                buildCountTypeString    : 'All',
                amountTypeString        : 'Builds',
                amount                  : 5,
                lessThan                : true,
                buildDurationMinutes    : 20
        ] | ''
    }

    def 'buildStatusFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node buildStatusFilter = filters[0]
        buildStatusFilter.name() == 'hudson.views.BuildStatusFilter'
        buildStatusFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert buildStatusFilter.children()[idx].name() == name
            assert buildStatusFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    buildStatus()
                } | [
                includeExcludeTypeString: 'includeMatched',
                neverBuilt              : false,
                building                : false,
                inBuildQueue            : false
        ] | ''
                { ->
                    buildStatus {
                        matchType(INCLUDE_UNMATCHED)
                        neverBuilt()
                        building()
                        inBuildQueue()
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                neverBuilt              : true,
                building                : true,
                inBuildQueue            : true
        ] | ''
                { ->
                    buildStatus {
                        matchType(INCLUDE_UNMATCHED)
                        building()
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                neverBuilt              : false,
                building                : true,
                inBuildQueue            : false
        ] | ''
    }

    def 'buildTrendFilter'(Closure filter, Map children, def noop) {
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
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    buildTrend()
                } | [
                includeExcludeTypeString: 'includeMatched',
                buildCountTypeString    : 'Latest',
                amountTypeString        : 'Hours',
                amount                  : 0.0,
                statusTypeString        : 'Completed'
        ] | ''
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
        ] | ''
    }

    def 'fallbackFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node fallbackFilter = filters[0]
        fallbackFilter.name() == 'hudson.views.AddRemoveFallbackFilter'
        fallbackFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert fallbackFilter.children()[idx].name() == name
            assert fallbackFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    fallback()
                } | [
                fallbackTypeString: 'ADD_ALL_IF_NONE_INCLUDED',
                fallbackType      : 'ADD_ALL_IF_NONE_INCLUDED'
        ] | ''
                { ->
                    fallback {
                        type(FallbackType.REMOVE_ALL_IF_ALL_INCLUDED)
                    }
                } | [
                fallbackTypeString: 'REMOVE_ALL_IF_ALL_INCLUDED',
                fallbackType      : 'REMOVE_ALL_IF_ALL_INCLUDED'
        ] | ''
    }

    def 'jobTypeFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node jobTypeFilter = filters[0]
        jobTypeFilter.name() == 'hudson.views.JobTypeFilter'
        jobTypeFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert jobTypeFilter.children()[idx].name() == name
            assert jobTypeFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    type()
                } | [
                includeExcludeTypeString: 'includeMatched',
                jobType                 : 'hudson.model.FreeStyleProject'
        ] | ''
                { ->
                    type {
                        matchType(INCLUDE_UNMATCHED)
                        type(JobType.EXTERNAL_JOB)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                jobType                 : 'hudson.model.ExternalJob'
        ] | ''
                { ->
                    type {
                        matchType(EXCLUDE_UNMATCHED)
                        type(JobType.MATRIX_PROJECT)
                    }
                } | [
                includeExcludeTypeString: 'excludeUnmatched',
                jobType                 : 'hudson.matrix.MatrixProject'
        ] | ''
                { ->
                    type {
                        matchType(EXCLUDE_MATCHED)
                        type(JobType.MAVEN_MODULE_SET)
                    }
                } | [
                includeExcludeTypeString: 'excludeMatched',
                jobType                 : 'hudson.maven.MavenModuleSet'
        ] | ''
    }

    def 'mostRecentJobsFilter'(Closure filter, Map children, def noop) {
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
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    mostRecent()
                } | [
                maxToInclude  : 0,
                checkStartTime: false
        ] | ''
                { ->
                    mostRecent {
                        maxToInclude(5)
                        checkStartTime()
                    }
                } | [
                maxToInclude  : 5,
                checkStartTime: true
        ] | ''
    }

    def 'otherViewsFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node otherViewsFilter = filters[0]
        otherViewsFilter.name() == 'hudson.views.OtherViewsFilter'
        otherViewsFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert otherViewsFilter.children()[idx].name() == name
            assert otherViewsFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    otherViews()
                } | [
                includeExcludeTypeString: 'includeMatched',
                otherViewName           : ''
        ] | ''
                { ->
                    otherViews {
                        matchType(INCLUDE_UNMATCHED)
                        viewName('a view name')
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                otherViewName           : 'a view name'
        ] | ''
    }

    def 'parameterFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node parameterFilter = filters[0]
        parameterFilter.name() == 'hudson.views.ParameterFilter'
        parameterFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert parameterFilter.children()[idx].name() == name
            assert parameterFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    parameter()
                } | [
                includeExcludeTypeString: 'includeMatched',
                nameRegex               : '',
                valueRegex              : '',
                descriptionRegex        : '',
                useDefaultValue         : false,
                matchBuildsInProgress   : false,
                matchAllBuilds          : false,
                maxBuildsToMatch        : null
        ] | ''
                { ->
                    parameter {
                        matchType(INCLUDE_UNMATCHED)
                        nameRegex(/.*/)
                        valueRegex(/.*/)
                        descriptionRegex(/.*/)
                        useDefaultValue()
                        matchBuildsInProgress()
                        matchAllBuilds()
                        maxBuildsToMatch(123)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                nameRegex               : '.*',
                valueRegex              : '.*',
                descriptionRegex        : '.*',
                useDefaultValue         : true,
                matchBuildsInProgress   : true,
                matchAllBuilds          : true,
                maxBuildsToMatch        : 123
        ] | ''
    }

    def 'releaseJobsFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node releaseJobsFilter = filters[0]
        releaseJobsFilter.name() == 'hudson.plugins.release.ReleaseJobsFilter'
        releaseJobsFilter.children().size() == 0

        1 * jobManagement.requirePlugin('release')

        where:
        filter || children || noop
                { ->
                    release()
                } | [:] | ''
    }

    def 'scmTypeFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node scmTypeFilter = filters[0]
        scmTypeFilter.name() == 'hudson.views.ScmTypeFilter'
        scmTypeFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert scmTypeFilter.children()[idx].name() == name
            assert scmTypeFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    scm()
                } | [
                includeExcludeTypeString: 'includeMatched',
                scmType                 : 'hudson.scm.NullSCM'
        ] | ''
                { ->
                    scm {
                        matchType(INCLUDE_UNMATCHED)
                        type(ScmType.GIT)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                scmType                 : 'hudson.plugins.git.GitSCM'
        ] | ''
                { ->
                    scm {
                        type(ScmType.SVN)
                    }
                } | [
                includeExcludeTypeString: 'includeMatched',
                scmType                 : 'hudson.scm.SubversionSCM'
        ] | ''
    }

    def 'securedJobsFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node securedJobsFilter = filters[0]
        securedJobsFilter.name() == 'hudson.views.SecuredJobsFilter'
        securedJobsFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert securedJobsFilter.children()[idx].name() == name
            assert securedJobsFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    securedJobs()
                } | [
                includeExcludeTypeString: 'includeMatched'
        ] | ''
                { ->
                    securedJobs {
                        matchType(INCLUDE_UNMATCHED)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched'
        ] | ''
    }

    def 'securityFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node securityFilter = filters[0]
        securityFilter.name() == 'hudson.views.SecurityFilter'
        securityFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert securityFilter.children()[idx].name() == name
            assert securityFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    security()
                } | [
                includeExcludeTypeString: 'includeMatched',
                configure               : false,
                build                   : false,
                workspace               : false,
                permissionCheckType     : 'MustMatchAll'
        ] | ''
                { ->
                    security {
                        matchType(INCLUDE_UNMATCHED)
                        configurePermission()
                        buildPermission()
                        workspacePermission()
                        permissionCheck(PermissionCheckType.AT_LEAST_ONE)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                configure               : true,
                build                   : true,
                workspace               : true,
                permissionCheckType     : 'AtLeastOne'
        ] | ''
                { ->
                    security {
                        configurePermission()
                    }
                } | [
                includeExcludeTypeString: 'includeMatched',
                configure               : true,
                build                   : false,
                workspace               : false,
                permissionCheckType     : 'MustMatchAll'
        ] | ''
                { ->
                    security {
                        buildPermission()
                    }
                } | [
                includeExcludeTypeString: 'includeMatched',
                configure               : false,
                build                   : true,
                workspace               : false,
                permissionCheckType     : 'MustMatchAll'
        ] | ''
                { ->
                    security {
                        workspacePermission()
                    }
                } | [
                includeExcludeTypeString: 'includeMatched',
                configure               : false,
                build                   : false,
                workspace               : true,
                permissionCheckType     : 'MustMatchAll'
        ] | ''
    }

    def 'upstreamDownstreamJobsFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node upstreamDownstreamJobsFilter = filters[0]
        upstreamDownstreamJobsFilter.name() == 'hudson.views.UpstreamDownstreamJobsFilter'
        upstreamDownstreamJobsFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert upstreamDownstreamJobsFilter.children()[idx].name() == name
            assert upstreamDownstreamJobsFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    upstreamDownstream()
                } | [
                includeDownstream: false,
                includeUpstream  : false,
                recursive        : false,
                excludeOriginals : false
        ] | ''
                { ->
                    upstreamDownstream {
                        includeDownstream()
                        includeUpstream()
                        recursive()
                        excludeOriginals()
                    }
                } | [
                includeDownstream: true,
                includeUpstream  : true,
                recursive        : true,
                excludeOriginals : true
        ] | ''
                { ->
                    upstreamDownstream {
                        includeDownstream()
                    }
                } | [
                includeDownstream: true,
                includeUpstream  : false,
                recursive        : false,
                excludeOriginals : false
        ] | ''
                { ->
                    upstreamDownstream {
                        includeUpstream()
                    }
                } | [
                includeDownstream: false,
                includeUpstream  : true,
                recursive        : false,
                excludeOriginals : false
        ] | ''
                { ->
                    upstreamDownstream {
                        recursive()
                    }
                } | [
                includeDownstream: false,
                includeUpstream  : false,
                recursive        : true,
                excludeOriginals : false
        ] | ''
                { ->
                    upstreamDownstream {
                        excludeOriginals()
                    }
                } | [
                includeDownstream: false,
                includeUpstream  : false,
                recursive        : false,
                excludeOriginals : true
        ] | ''
    }

    def 'unclassifiedJobsFilter'(Closure filter, Map children, def noop) {
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
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    unclassified()
                } | [
                includeExcludeTypeString: 'includeMatched'
        ] | ''
                { ->
                    unclassified {
                        matchType(INCLUDE_UNMATCHED)
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched'
        ] | ''
    }

    def 'userRelevanceFilter'(Closure filter, Map children, def noop) {
        when:
        view.jobFilters(filter)

        then:
        def filters = view.node.jobFilters[0].value()
        filters.size() == 1

        Node userRelevanceFilter = filters[0]
        userRelevanceFilter.name() == 'hudson.views.UserRelevanceFilter'
        userRelevanceFilter.children().size() == children.size()
        children.eachWithIndex { name, value, idx ->
            assert userRelevanceFilter.children()[idx].name() == name
            assert userRelevanceFilter.children()[idx].value() == value
        }
        1 * jobManagement.requirePlugin('view-job-filters')

        where:
        filter || children || noop
                { ->
                    userRelevance()
                } | [
                includeExcludeTypeString: 'includeMatched',
                buildCountTypeString    : 'Latest',
                amountTypeString        : 'Hours',
                amount                  : 0.0,
                matchUserId             : false,
                matchUserFullName       : false,
                ignoreCase              : false,
                ignoreWhitespace        : false,
                ignoreNonAlphaNumeric   : false,
                matchBuilder            : false,
                matchEmail              : false,
                matchScmChanges         : false
        ] | ''
                { ->
                    userRelevance {
                        matchType(INCLUDE_UNMATCHED)
                        buildCountType(BuildCountType.AT_LEAST_ONE)
                        amountType(AmountType.DAYS)
                        amount(2.5)
                        matchUserId()
                        matchUserFullName()
                        ignoreCase()
                        ignoreWhitespace()
                        ignoreNonAlphaNumeric()
                        matchBuilder()
                        matchEmail()
                        matchScmChanges()
                    }
                } | [
                includeExcludeTypeString: 'includeUnmatched',
                buildCountTypeString    : 'AtLeastOne',
                amountTypeString        : 'Days',
                amount                  : 2.5,
                matchUserId             : true,
                matchUserFullName       : true,
                ignoreCase              : true,
                ignoreWhitespace        : true,
                ignoreNonAlphaNumeric   : true,
                matchBuilder            : true,
                matchEmail              : true,
                matchScmChanges         : true
        ] | ''
                { ->
                    userRelevance {
                        buildCountType(BuildCountType.ALL)
                        amountType(AmountType.BUILDS)
                        amount(15)
                        matchUserId()
                        ignoreCase()
                        ignoreNonAlphaNumeric()
                        matchEmail()
                    }
                } | [
                includeExcludeTypeString: 'includeMatched',
                buildCountTypeString    : 'All',
                amountTypeString        : 'Builds',
                amount                  : 15,
                matchUserId             : true,
                matchUserFullName       : false,
                ignoreCase              : true,
                ignoreWhitespace        : false,
                ignoreNonAlphaNumeric   : true,
                matchBuilder            : false,
                matchEmail              : true,
                matchScmChanges         : false
        ] | ''
    }

}
