listView('example') {
    jobFilters {
        userRelevance {
            matchType(MatchType.INCLUDE_UNMATCHED)
            buildCountType(BuildCountType.AT_LEAST_ONE)
            amountType(AmountType.DAYS)
            amount(2.5)
            matchUserId()
            matchUserFullName()
            ignoreCase()
            ignoreWhitespace()
            ignoreNonAlphaNumeric()
            matchBuilder()
            matchEmail()
            matchScmChanges()
        }
    }
}
