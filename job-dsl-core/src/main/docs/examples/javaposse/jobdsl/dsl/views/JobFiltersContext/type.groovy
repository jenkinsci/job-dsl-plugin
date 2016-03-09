listView('example') {
    jobFilters {
        type {
            matchType(MatchType.INCLUDE_UNMATCHED)
            type(JobType.EXTERNAL_JOB)
        }
    }
}
