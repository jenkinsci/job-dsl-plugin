job('example') {
    triggers {
        gitlabPush {
            buildOnMergeRequestEvents(false)
            buildOnPushEvents(false)
            enableCiSkip(false)
            setBuildDescription(false)
            rebuildOpenMergeRequest('never')
            includeBranches('include1,include2')
            excludeBranches('exclude1,exclude2')
        }
    }
}
