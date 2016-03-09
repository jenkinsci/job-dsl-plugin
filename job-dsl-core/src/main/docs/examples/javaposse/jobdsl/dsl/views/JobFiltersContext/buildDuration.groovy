listView('example') {
    jobFilters {
        buildDuration {
            matchType(MatchType.INCLUDE_UNMATCHED)
            buildCountType(BuildCountType.AT_LEAST_ONE)
            amountType(AmountType.DAYS)
            amount(1.5)
            lessThan()
            buildDuration(6.2)
        }
    }
}
