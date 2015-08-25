package javaposse.jobdsl.dsl.views.jobfilter

import javaposse.jobdsl.dsl.Context

abstract class AbstractJobFilter implements Context {
    MatchType matchType = MatchType.INCLUDE_MATCHED

    /**
     * Specifies whether the filter includes or excludes jobs from the view.
     * Possible values are {@code MatchType.INCLUDE_MATCHED}, {@code MatchType.INCLUDE_UNMATCHED},
     * {@code MatchType.EXCLUDE_MATCHED} or {@code MatchType.EXCLUDE_UNMATCHED}.
     */
    void matchType(MatchType matchType) {
        this.matchType = matchType
    }
}
