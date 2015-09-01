job('example') {
    publishers {
        mergePullRequest {
            mergeComment('merged by Jenkins')
            disallowOwnCode()
        }
    }
}
