listView('example') {
    jobFilters {
        security {
            matchType(MatchType.INCLUDE_UNMATCHED)
            configurePermission()
            buildPermission()
            workspacePermission()
            permissionCheck(PermissionCheckType.AT_LEAST_ONE)
        }
    }
}
