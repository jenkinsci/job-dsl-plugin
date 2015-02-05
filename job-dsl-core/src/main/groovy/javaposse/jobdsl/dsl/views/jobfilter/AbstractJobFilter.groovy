package javaposse.jobdsl.dsl.views.jobfilter

import javaposse.jobdsl.dsl.Context

abstract class AbstractJobFilter implements Context {
    MatchType matchType = MatchType.INCLUDE_MATCHED
    abstract String getClassName()

    Node getNode() {
        new NodeBuilder()."${className}" {
            addArgs(delegate)
        }
    }

    protected void addArgs(NodeBuilder builder) {
        builder.includeExcludeTypeString(matchType.value)
    }

    void matchType(MatchType matchType) {
        this.matchType = matchType
    }
}
