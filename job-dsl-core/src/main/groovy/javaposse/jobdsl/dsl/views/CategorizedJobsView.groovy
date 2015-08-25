package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class CategorizedJobsView extends ListView {
    CategorizedJobsView(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds grouping rules.
     */
    void categorizationCriteria(@DslContext(CategorizationCriteriaContext) Closure categorizationCriteriaClosure) {
        CategorizationCriteriaContext context = new CategorizationCriteriaContext()
        executeInContext(categorizationCriteriaClosure, context)

        execute {
            context.groupingRules.each { groupingRule ->
                it / 'categorizationCriteria' << groupingRule
            }
        }
    }
}
