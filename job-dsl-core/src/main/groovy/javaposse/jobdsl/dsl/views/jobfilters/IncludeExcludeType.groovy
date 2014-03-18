package javaposse.jobdsl.dsl.views.jobfilters

enum IncludeExcludeType {
    INCLUDE_MATCHED('includeMatched'), INCLUDE_UNMATCHED('includeUnmatched'), EXCLUDE_MATCHED('excludeMatched'), EXCLUDE_UNMATCHED('excludeUnmatched')
    
    final String value

    IncludeExcludeType(String value) {
        this.value = value
    }
}
