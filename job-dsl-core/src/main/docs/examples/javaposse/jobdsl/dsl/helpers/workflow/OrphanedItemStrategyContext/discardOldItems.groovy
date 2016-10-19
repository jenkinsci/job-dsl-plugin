multibranchPipelineJob('example') {
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}
