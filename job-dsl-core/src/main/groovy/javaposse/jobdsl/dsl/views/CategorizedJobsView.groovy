package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class CategorizedJobsView extends ListView {
    CategorizedJobsView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds grouping rules.
     */
    void categorizationCriteria(@DslContext(CategorizationCriteriaContext) Closure categorizationCriteriaClosure) {
        CategorizationCriteriaContext context = new CategorizationCriteriaContext()
        executeInContext(categorizationCriteriaClosure, context)

        configure {
            context.groupingRules.each { groupingRule ->
                it / 'categorizationCriteria' << groupingRule
            }
        }
    }

    /**
     * Shows only the category name on category header rows instead of aggregated job information. Defaults to
     * {@code false}.
     *
     * @since 1.95
     */
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '246.ve02ea_a_a_d21a_7')
    void hideAggregatedInfoOnCategories(boolean hideAggregatedInfoOnCategories = true) {
        configure {
            it / methodMissing('hideAggregatedInfoOnCategories', hideAggregatedInfoOnCategories)
        }
    }

    /**
     * Treats {@code /} in group names as a separator for nested category groups. Defaults to {@code false}.
     *
     * @since 1.95
     */
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '246.ve02ea_a_a_d21a_7')
    void nestedGroups(boolean nestedGroups = true) {
        configure {
            it / methodMissing('nestedGroups', nestedGroups)
        }
    }
}
