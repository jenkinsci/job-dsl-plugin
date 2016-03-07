package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.views.jobfilter.BuildDurationFilter
import javaposse.jobdsl.dsl.views.jobfilter.BuildStatusFilter
import javaposse.jobdsl.dsl.views.jobfilter.BuildTrendFilter
import javaposse.jobdsl.dsl.views.jobfilter.FallbackFilter
import javaposse.jobdsl.dsl.views.jobfilter.JobStatusesFilter
import javaposse.jobdsl.dsl.views.jobfilter.JobTypeFilter
import javaposse.jobdsl.dsl.views.jobfilter.MostRecentJobsFilter
import javaposse.jobdsl.dsl.views.jobfilter.OtherViewsFilter
import javaposse.jobdsl.dsl.views.jobfilter.ParameterFilter
import javaposse.jobdsl.dsl.views.jobfilter.RegexFilter
import javaposse.jobdsl.dsl.views.jobfilter.ScmTypeFilter
import javaposse.jobdsl.dsl.views.jobfilter.SecuredJobsFilter
import javaposse.jobdsl.dsl.views.jobfilter.SecurityFilter
import javaposse.jobdsl.dsl.views.jobfilter.Status
import javaposse.jobdsl.dsl.views.jobfilter.UnclassifiedJobsFilter
import javaposse.jobdsl.dsl.views.jobfilter.UpstreamDownstreamJobsFilter
import javaposse.jobdsl.dsl.views.jobfilter.UserRelevanceFilter

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class JobFiltersContext extends AbstractContext {
    List<Node> filterNodes = []

    protected JobFiltersContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds an all job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void all() {
        filterNodes << new NodeBuilder().'hudson.views.AllJobsFilter'()
    }

    /**
     * Adds an all release job filter.
     * Adds to the view all the jobs using the 'release' build wrapper.
     */
    @RequiresPlugin(id = 'release')
    void allRelease() {
        filterNodes << new NodeBuilder().'hudson.views.AllReleaseJobsFilter'()
    }

    /**
     * Adds a build duration job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void buildDuration(@DslContext(BuildDurationFilter) Closure buildDurationFilterClosure) {
        BuildDurationFilter buildDurationFilter = new BuildDurationFilter()
        executeInContext(buildDurationFilterClosure, buildDurationFilter)

        filterNodes << new NodeBuilder().'hudson.views.BuildDurationFilter' {
            includeExcludeTypeString(buildDurationFilter.matchType.value)
            buildCountTypeString(buildDurationFilter.buildCountType.value)
            amountTypeString(buildDurationFilter.amountType.value)
            amount(buildDurationFilter.amount)
            lessThan(buildDurationFilter.lessThan)
            buildDurationMinutes(buildDurationFilter.buildDuration)
        }
    }

    /**
     * Adds a job build status filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void buildStatus(@DslContext(BuildStatusFilter) Closure buildStatusFilterClosure) {
        BuildStatusFilter buildStatusFilter = new BuildStatusFilter()
        executeInContext(buildStatusFilterClosure, buildStatusFilter)

        filterNodes << new NodeBuilder().'hudson.views.BuildStatusFilter' {
            includeExcludeTypeString(buildStatusFilter.matchType.value)
            neverBuilt(buildStatusFilter.neverBuilt)
            building(buildStatusFilter.building)
            inBuildQueue(buildStatusFilter.inBuildQueue)
        }
    }

    /**
     * Adds a build trend job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void buildTrend(@DslContext(BuildTrendFilter) Closure buildTrendFilterClosure) {
        BuildTrendFilter buildTrendFilter = new BuildTrendFilter()
        executeInContext(buildTrendFilterClosure, buildTrendFilter)

        filterNodes << new NodeBuilder().'hudson.views.BuildTrendFilter' {
            includeExcludeTypeString(buildTrendFilter.matchType.value)
            buildCountTypeString(buildTrendFilter.buildCountType.value)
            amountTypeString(buildTrendFilter.amountType.value)
            amount(buildTrendFilter.amount)
            statusTypeString(buildTrendFilter.status.value)
        }
    }

    /**
     * Adds a fallback job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void fallback(@DslContext(FallbackFilter) Closure fallbackFilterClosure) {
        FallbackFilter fallbackFilter = new FallbackFilter()
        executeInContext(fallbackFilterClosure, fallbackFilter)

        filterNodes << new NodeBuilder().'hudson.views.AddRemoveFallbackFilter' {
            fallbackTypeString(fallbackFilter.type.name())
            fallbackType(fallbackFilter.type.name())
        }
    }

    /**
     * Adds a most recent job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void mostRecent(@DslContext(MostRecentJobsFilter) Closure mostRecentFilterClosure) {
        MostRecentJobsFilter mostRecentFilter = new MostRecentJobsFilter()
        executeInContext(mostRecentFilterClosure, mostRecentFilter)

        filterNodes << new NodeBuilder().'hudson.views.MostRecentJobsFilter' {
            maxToInclude(mostRecentFilter.maxToInclude)
            checkStartTime(mostRecentFilter.checkStartTime)
        }
    }

    /**
     * Adds an other views job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void otherViews(@DslContext(OtherViewsFilter) Closure otherViewsFilterClosure) {
        OtherViewsFilter otherViewsFilter = new OtherViewsFilter()
        executeInContext(otherViewsFilterClosure, otherViewsFilter)

        filterNodes << new NodeBuilder().'hudson.views.OtherViewsFilter' {
            includeExcludeTypeString(otherViewsFilter.matchType.value)
            otherViewName(otherViewsFilter.viewName)
        }
    }

    /**
     * Add a filter by job parameterization.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void parameter(@DslContext(ParameterFilter) Closure parameterFilterClosure) {
        ParameterFilter parameterFilter = new ParameterFilter()
        executeInContext(parameterFilterClosure, parameterFilter)

        filterNodes << new NodeBuilder().'hudson.views.ParameterFilter' {
            includeExcludeTypeString(parameterFilter.matchType.value)
            nameRegex(parameterFilter.nameRegex)
            valueRegex(parameterFilter.valueRegex)
            descriptionRegex(parameterFilter.descriptionRegex)
            useDefaultValue(parameterFilter.useDefaultValue)
            matchBuildsInProgress(parameterFilter.matchBuildsInProgress)
            matchAllBuilds(parameterFilter.matchAllBuilds)
            maxBuildsToMatch(parameterFilter.maxBuildsToMatch)
        }
    }

    /**
     * Add a release job filter.
     * Filters the view to only keep the jobs using the 'release' build wrapper.
     */
    @RequiresPlugin(id = 'release')
    void release() {
        filterNodes << new NodeBuilder().'hudson.plugins.release.ReleaseJobsFilter'()
    }

    /**
     * Adds a regular expression filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void regex(@DslContext(RegexFilter) Closure regexFilterClosure) {
        RegexFilter regexFilter = new RegexFilter()
        executeInContext(regexFilterClosure, regexFilter)

        filterNodes << new NodeBuilder().'hudson.views.RegExJobFilter' {
            includeExcludeTypeString(regexFilter.matchType.value)
            valueTypeString(regexFilter.matchValue.name())
            delegate.regex(regexFilter.regex)
        }
    }

    /**
     * Add a project-based secured jobs Filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void securedJobs(@DslContext(SecuredJobsFilter) Closure regexFilterClosure) {
        SecuredJobsFilter regexFilter = new SecuredJobsFilter()
        executeInContext(regexFilterClosure, regexFilter)

        filterNodes << new NodeBuilder().'hudson.views.SecuredJobsFilter' {
            includeExcludeTypeString(regexFilter.matchType.value)
        }
    }

    /**
     * Add a user permissions job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void security(@DslContext(SecurityFilter) Closure securityFilterClosure) {
        SecurityFilter securityFilter = new SecurityFilter()
        executeInContext(securityFilterClosure, securityFilter)

        filterNodes << new NodeBuilder().'hudson.views.SecurityFilter' {
            includeExcludeTypeString(securityFilter.matchType.value)
            configure(securityFilter.configurePermission)
            build(securityFilter.buildPermission)
            workspace(securityFilter.workspacePermission)
            permissionCheckType(securityFilter.permissionCheck.value)
        }
    }

    /**
     * Adds a job status filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void status(@DslContext(JobStatusesFilter) Closure statusesFilterClosure) {
        JobStatusesFilter statusesFilter = new JobStatusesFilter()
        executeInContext(statusesFilterClosure, statusesFilter)

        filterNodes << new NodeBuilder().'hudson.views.JobStatusFilter' {
            includeExcludeTypeString(statusesFilter.matchType.value)
            Status.values().each { status ->
                "${status.name().toLowerCase()}"(statusesFilter.status.contains(status))
            }
        }
    }

    /**
     * Adds a SCM job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void scm(@DslContext(ScmTypeFilter) Closure scmTypeFilterClosure) {
        ScmTypeFilter scmTypeFilter = new ScmTypeFilter()
        executeInContext(scmTypeFilterClosure, scmTypeFilter)

        filterNodes << new NodeBuilder().'hudson.views.ScmTypeFilter' {
            includeExcludeTypeString(scmTypeFilter.matchType.value)
            scmType(scmTypeFilter.type.value)
        }
    }

    /**
     * Adds a job type filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void type(@DslContext(JobTypeFilter) Closure jobTypeFilterClosure) {
        JobTypeFilter jobTypeFilter = new JobTypeFilter()
        executeInContext(jobTypeFilterClosure, jobTypeFilter)

        filterNodes << new NodeBuilder().'hudson.views.JobTypeFilter' {
            includeExcludeTypeString(jobTypeFilter.matchType.value)
            jobType(jobTypeFilter.type.value)
        }
    }

    /**
     * Adds an unclassified jobs filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void unclassified(@DslContext(UnclassifiedJobsFilter) Closure unclassifiedJobsFilterClosure) {
        UnclassifiedJobsFilter unclassifiedJobsFilter = new UnclassifiedJobsFilter()
        executeInContext(unclassifiedJobsFilterClosure, unclassifiedJobsFilter)

        filterNodes << new NodeBuilder().'hudson.views.UnclassifiedJobsFilter' {
            includeExcludeTypeString(unclassifiedJobsFilter.matchType.value)
        }
    }

    /**
     * Add a upstream/downstream jobs filter
     */
    @RequiresPlugin(id = 'view-job-filters')
    void upstreamDownstream(@DslContext(UpstreamDownstreamJobsFilter) Closure upstreamDownstreamJobsFilterClosure) {
        UpstreamDownstreamJobsFilter upstreamDownstreamJobsFilter = new UpstreamDownstreamJobsFilter()
        executeInContext(upstreamDownstreamJobsFilterClosure, upstreamDownstreamJobsFilter)

        filterNodes << new NodeBuilder().'hudson.views.UpstreamDownstreamJobsFilter' {
            includeDownstream(upstreamDownstreamJobsFilter.includeDownstream)
            includeUpstream(upstreamDownstreamJobsFilter.includeUpstream)
            recursive(upstreamDownstreamJobsFilter.recursive)
            excludeOriginals(upstreamDownstreamJobsFilter.excludeOriginals)
        }
    }

    /**
     * Adds an user relevance job filter.
     */
    @RequiresPlugin(id = 'view-job-filters')
    void userRelevance(@DslContext(UserRelevanceFilter) Closure userRelevanceFilterClosure) {
        UserRelevanceFilter userRelevanceFilter = new UserRelevanceFilter()
        executeInContext(userRelevanceFilterClosure, userRelevanceFilter)

        filterNodes << new NodeBuilder().'hudson.views.UserRelevanceFilter' {
            includeExcludeTypeString(userRelevanceFilter.matchType.value)
            buildCountTypeString(userRelevanceFilter.buildCountType.value)
            amountTypeString(userRelevanceFilter.amountType.value)
            amount(userRelevanceFilter.amount)
            matchUserId(userRelevanceFilter.matchUserId)
            matchUserFullName(userRelevanceFilter.matchUserFullName)
            ignoreCase(userRelevanceFilter.ignoreCase)
            ignoreWhitespace(userRelevanceFilter.ignoreWhitespace)
            ignoreNonAlphaNumeric(userRelevanceFilter.ignoreNonAlphaNumeric)
            matchBuilder(userRelevanceFilter.matchBuilder)
            matchEmail(userRelevanceFilter.matchEmail)
            matchScmChanges(userRelevanceFilter.matchScmChanges)
        }
    }

}
