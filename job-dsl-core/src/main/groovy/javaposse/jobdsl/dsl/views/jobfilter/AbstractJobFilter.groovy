package javaposse.jobdsl.dsl.views.jobfilter

import javaposse.jobdsl.dsl.Context

abstract class AbstractJobFilter implements Context {
    MatchType matchType = MatchType.INCLUDE_MATCHED

    /**
     * Specifies whether the filter includes or excludes jobs from the view. Defaults to
     * {@code MatchType.INCLUDE_MATCHED}.
     */
    void matchType(MatchType matchType) {
        this.matchType = matchType
    }
}
