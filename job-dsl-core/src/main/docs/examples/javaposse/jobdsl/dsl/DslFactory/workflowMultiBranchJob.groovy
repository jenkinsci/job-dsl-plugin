workflowMultiBranchJob('example') {
    branchSource {
        git {
            remote('git://localhost/repo')
            credentialsId('git-user-credentials-id')
            includes('*')
            excludes('')
            ignoreOnPushNotifications(false)
        }
    }
    orphanedItemStrategy {
        pruneDeadBranches(true)
        daysToKeep(0)
        numToKeep(0)
    }
}
