package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.views.jobfilter.JobStatusesFilter
import javaposse.jobdsl.dsl.views.jobfilter.RegexFilter

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class JobFiltersContext implements Context {
    List<Node> filterNodes = []

    void status(@DslContext(JobStatusesFilter) Closure statusesFilterClosure) {
        JobStatusesFilter statusesFilter = new JobStatusesFilter()
        executeInContext(statusesFilterClosure, statusesFilter)

        filterNodes << statusesFilter.node
    }

    void regex(@DslContext(RegexFilter) Closure regexFilterClosure) {
        RegexFilter regexFilter = new RegexFilter()
        executeInContext(regexFilterClosure, regexFilter)

        filterNodes << regexFilter.node
    }
}
