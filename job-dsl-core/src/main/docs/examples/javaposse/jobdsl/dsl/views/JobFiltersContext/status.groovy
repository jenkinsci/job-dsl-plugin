listView('example') {
    jobFilters {
        status {
            matchType(MatchType.INCLUDE_MATCHED)
            status(Status.FAILED)
        }
    }
}
