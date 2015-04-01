package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

/**
 * For {@link javaposse.jobdsl.dsl.views.CategorizedJobsView}
 * uses {@link javaposse.jobdsl.dsl.views.GroupingRuleContext}
 */
class CategorizationCriteriaContext implements Context {
    private final JobManagement jobManagement
    List<Node> groupingRules = []

    CategorizationCriteriaContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void groupingRule(@DslContext(GroupingRuleContext) Closure groupingRuleClosure) {
        GroupingRuleContext context = new GroupingRuleContext(jobManagement)
        executeInContext(groupingRuleClosure, context)

        groupingRules << new NodeBuilder().'org.jenkinsci.plugins.categorizedview.GroupingRule' {
            groupRegex(context.groupRegex)
            namingRule(context.namingRule)
        }
    }

    void byRegexWithNaming(String groupRegex, String namingRule) {
        groupingRule {
            delegate.groupRegex(groupRegex)
            delegate.namingRule(namingRule)
        }
    }
}
