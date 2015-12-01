job('example') {
    scm {
        git {
            remote {
                name('origin')
                url('git@serverA:account/repo1.git')
            }
        }
    }
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
            allowAllBranches(false)
            includeBranches('include1,include2')
            excludeBranches('exclude1,exclude2')
        }
    }
}
