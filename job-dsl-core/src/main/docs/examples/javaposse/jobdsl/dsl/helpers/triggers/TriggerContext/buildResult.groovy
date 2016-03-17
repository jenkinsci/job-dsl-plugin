job('example') {
    triggers {
        buildResult('H/10 * * * *') {
            combinedJobs()
            triggerInfo('My job 1', BuildResult.SUCCESS, BuildResult.UNSTABLE, BuildResult.FAILURE,
                        BuildResult.NOT_BUILT, BuildResult.ABORTED)
            triggerInfo('My job 2', BuildResult.SUCCESS, BuildResult.UNSTABLE, BuildResult.FAILURE,
                    BuildResult.NOT_BUILT)
            triggerInfo('My job 3, My job 4', BuildResult.SUCCESS, BuildResult.UNSTABLE, BuildResult.FAILURE)
            triggerInfo('My job 5', BuildResult.SUCCESS, BuildResult.UNSTABLE)
            triggerInfo('My job 6, My job 7')
        }
    }
}
