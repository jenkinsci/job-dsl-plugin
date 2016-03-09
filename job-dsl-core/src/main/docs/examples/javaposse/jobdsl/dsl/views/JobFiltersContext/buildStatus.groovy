listView('example') {
    jobFilters {
        buildStatus {
            matchType(MatchType.INCLUDE_UNMATCHED)
            neverBuilt()
            building()
            inBuildQueue()
        }
    }
}
