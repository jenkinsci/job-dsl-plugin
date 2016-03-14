multibranchWorkflowJob('example') {
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}
