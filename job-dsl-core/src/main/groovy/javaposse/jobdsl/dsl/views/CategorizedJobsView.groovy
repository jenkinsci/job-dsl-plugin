package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

/**
 * [Categorized Jobs View](https://wiki.jenkins-ci.org/display/JENKINS/Categorized+Jobs+View)
 *
 * Usage example:
 *
 * <pre>
 *    categorizedJobsView("Configuration") {
 *          // same as in listView
 *          jobs {
 *              regex("configuration_.*")
 *          }
 *
 *          categorizationCriteria {
 *              groupingRule {
 *                  groupRegex("regex")
 *                  namingRule("naming")
 *              }
 *              // short alias
 *              byRegexWithNaming('^configuration_([^_]+).*$', '$1')
 *          }
 *
 *          // same as in listView
 *          columns {
 *              status()
 *              name()
 *              buildButton()
 *          }
 *   }
 *
 * </pre>
 *
 * @since 1.31
 */
class CategorizedJobsView extends ListView {
    CategorizedJobsView(JobManagement jobManagement) {
        super(jobManagement)
    }

    void categorizationCriteria(@DslContext(CategorizationCriteriaContext) Closure categorizationCriteriaClosure) {
        CategorizationCriteriaContext context = new CategorizationCriteriaContext(jobManagement)
        executeInContext(categorizationCriteriaClosure, context)

        execute {
            context.groupingRules.each { groupingRule ->
                it / 'categorizationCriteria' << groupingRule
            }
        }
    }
}
