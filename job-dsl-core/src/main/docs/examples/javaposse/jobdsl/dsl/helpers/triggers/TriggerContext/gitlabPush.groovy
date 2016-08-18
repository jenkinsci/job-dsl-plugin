job('example') {
    triggers {
        gitlabPush {
            buildOnMergeRequestEvents(false)
            buildOnPushEvents(false)
            enableCiSkip(false)
            setBuildDescription(false)
            addNoteOnMergeRequest(false)
            rebuildOpenMergeRequest('never')
            addVoteOnMergeRequest(false)
            useCiFeatures(false)
            acceptMergeRequestOnSuccess()
            includeBranches('include1,include2')
            excludeBranches('exclude1,exclude2')
        }
    }
}
