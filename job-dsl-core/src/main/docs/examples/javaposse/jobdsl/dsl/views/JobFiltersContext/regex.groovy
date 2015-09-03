listView('example') {
    jobFilters {
        regex {
            matchType(MatchType.EXCLUDE_MATCHED)
            matchValue(RegexMatchValue.DESCRIPTION)
            regex('.*project-a.*')
        }
    }
}
