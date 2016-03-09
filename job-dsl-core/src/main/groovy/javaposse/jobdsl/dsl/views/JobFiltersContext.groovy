package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.views.jobfilter.JobStatusesFilter
import javaposse.jobdsl.dsl.views.jobfilter.MostRecentJobsFilter
import javaposse.jobdsl.dsl.views.jobfilter.RegexFilter
import javaposse.jobdsl.dsl.views.jobfilter.Status
import javaposse.jobdsl.dsl.views.jobfilter.UnclassifiedJobsFilter

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class JobFiltersContext extends AbstractContext {
    List<Node> filterNodes = []

    protected JobFiltersContext(JobManagement jobManagement) {
        super(jobManagement)
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

}
