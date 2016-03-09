listView('example') {
    jobFilters {
        parameter {
            matchType(MatchType.INCLUDE_UNMATCHED)
            nameRegex(/.*/)
            valueRegex(/.*/)
            descriptionRegex(/.*/)
            useDefaultValue()
            matchBuildsInProgress()
            matchAllBuilds()
            maxBuildsToMatch(123)
        }
    }
}
