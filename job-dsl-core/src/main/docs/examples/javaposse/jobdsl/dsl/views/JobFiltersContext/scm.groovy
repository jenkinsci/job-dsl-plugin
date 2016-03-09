listView('example') {
    jobFilters {
        scm {
            matchType(MatchType.INCLUDE_UNMATCHED)
            type(ScmType.GIT)
        }
    }
}
