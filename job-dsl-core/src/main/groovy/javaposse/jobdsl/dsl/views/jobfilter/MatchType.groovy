package javaposse.jobdsl.dsl.views.jobfilter

enum MatchType {
    INCLUDE_MATCHED('includeMatched'),
    INCLUDE_UNMATCHED('includeUnmatched'),
    EXCLUDE_MATCHED('excludeMatched'),
    EXCLUDE_UNMATCHED('excludeUnmatched')

    final String value

    MatchType(String value) {
        this.value = value
    }
}
