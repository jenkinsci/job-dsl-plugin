listView('example') {
    jobFilters {
        buildTrend {
            matchType(MatchType.INCLUDE_UNMATCHED)
            buildCountType(BuildCountType.AT_LEAST_ONE)
            amountType(AmountType.DAYS)
            amount(2.5)
            status(BuildStatusType.TRIGGERED_BY_REMOTE)
        }
    }
}
