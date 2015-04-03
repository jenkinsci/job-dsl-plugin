package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

class CategorizationCriteriaContext implements Context {
    List<Node> groupingRules = []

    void regexGroupingRule(String groupRegex, String namingRule = null) {
        groupingRules << new NodeBuilder().'org.jenkinsci.plugins.categorizedview.GroupingRule' {
            delegate.groupRegex(groupRegex)
            delegate.namingRule(namingRule ?: '')
        }
    }
}
