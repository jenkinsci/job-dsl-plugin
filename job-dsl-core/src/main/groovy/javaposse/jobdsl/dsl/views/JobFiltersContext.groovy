package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.views.jobfilter.BuildTrendFilter
import javaposse.jobdsl.dsl.views.jobfilter.JobStatusesFilter
import javaposse.jobdsl.dsl.views.jobfilter.MostRecentJobsFilter
import javaposse.jobdsl.dsl.views.jobfilter.RegexFilter
import javaposse.jobdsl.dsl.views.jobfilter.Status
import javaposse.jobdsl.dsl.views.jobfilter.UnclassifiedJobsFilter

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

@ContextType('hudson.views.ViewJobFilter')
class JobFiltersContext extends AbstractExtensibleContext {
    List<Node> filterNodes = []

    protected JobFiltersContext(JobManagement jobManagement) {
        super(jobManagement, null)
    }

    @Override
    protected void addExtensionNode(Node node) {
        filterNodes << node
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
     * Adds a build trend job filter.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'view-job-filters', minimumVersion = '1.27')
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
     * Adds a most recent job filter.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'view-job-filters', minimumVersion = '1.27')
    void mostRecent(@DslContext(MostRecentJobsFilter) Closure mostRecentFilterClosure) {
        MostRecentJobsFilter mostRecentFilter = new MostRecentJobsFilter()
        executeInContext(mostRecentFilterClosure, mostRecentFilter)

        filterNodes << new NodeBuilder().'hudson.views.MostRecentJobsFilter' {
            maxToInclude(mostRecentFilter.maxToInclude)
            checkStartTime(mostRecentFilter.checkStartTime)
        }
    }

    /**
     * Adds an unclassified jobs filter.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'view-job-filters', minimumVersion = '1.27')
    void unclassified(@DslContext(UnclassifiedJobsFilter) Closure unclassifiedJobsFilterClosure) {
        UnclassifiedJobsFilter unclassifiedJobsFilter = new UnclassifiedJobsFilter()
        executeInContext(unclassifiedJobsFilterClosure, unclassifiedJobsFilter)

        filterNodes << new NodeBuilder().'hudson.views.UnclassifiedJobsFilter' {
            includeExcludeTypeString(unclassifiedJobsFilter.matchType.value)
        }
    }

    /**
     * Adds an all jobs to the view.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'view-job-filters', minimumVersion = '1.27')
    void all() {
        filterNodes << new NodeBuilder().'hudson.views.AllJobsFilter'()
    }

    /**
     * Adds to the view all the jobs using the 'release' build wrapper.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.5.3')
    void allRelease() {
        filterNodes << new NodeBuilder().'hudson.plugins.release.AllReleaseJobsFilter'()
    }

    /**
     * Filters the view to only keep the jobs using the 'release' build wrapper.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.5.3')
    void release() {
        filterNodes << new NodeBuilder().'hudson.plugins.release.ReleaseJobsFilter'()
    }
}
