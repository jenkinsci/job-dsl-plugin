workflowMultiBranchJob('example') {
    orphanedItemStrategy {
        pruneDeadBranches(true)
        daysToKeep(0)
        numToKeep(0)
    }
}
