categorizedJobsView('example') {
    jobFilters {
        regex {
            matchType(MatchType.EXCLUDE_MATCHED)
            matchValue(RegexMatchValue.DESCRIPTION)
            regex('.*project-a.*')
        }
        status {
            matchType(MatchType.INCLUDE_MATCHED)
            status(Status.FAILED)
        }
    }
}
