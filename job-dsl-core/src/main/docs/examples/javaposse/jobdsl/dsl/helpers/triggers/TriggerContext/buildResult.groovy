job('example') {
    triggers {
        buildResult('H/10 * * * *') {
            combinedJobs()
            triggerInfo('job-1, job-2', BuildResult.SUCCESS, BuildResult.UNSTABLE, BuildResult.FAILURE)
            triggerInfo('job-3', BuildResult.SUCCESS, BuildResult.UNSTABLE)
            triggerInfo('job-4, job-5')
        }
    }
}
