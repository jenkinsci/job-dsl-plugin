package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.views.jobfilter.JobStatusesFilter
import javaposse.jobdsl.dsl.views.jobfilter.RegexFilter
import javaposse.jobdsl.dsl.views.jobfilter.Status

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
}
