package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

/**
 * For {@link javaposse.jobdsl.dsl.views.CategorizationCriteriaContext}
 */
class GroupingRuleContext implements Context {
    private final JobManagement jobManagement
    String groupRegex
    String namingRule

    GroupingRuleContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void groupRegex(String groupRegex) {
        this.groupRegex = groupRegex
    }

    void namingRule(String namingRule) {
        this.namingRule = namingRule
    }
}
