categorizedJobsView('example') {
    nestedGroups()
    categorizationCriteria {
        regexGroupingRule(/^team-a_.*-build$/, 'Team A/Builds')
        regexGroupingRule(/^team-a_.*-deploy$/, 'Team A/Deployments')
    }
}
