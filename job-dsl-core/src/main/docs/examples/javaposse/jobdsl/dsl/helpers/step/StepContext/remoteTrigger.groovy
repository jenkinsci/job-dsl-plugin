// start the job 'test-flow' on the Jenkins instance named 'test-ci' without parameters
job('example-1') {
    steps {
        remoteTrigger('test-ci', 'test-flow')
    }
}

// start the job 'test-flow' on the Jenkins instance named 'test-ci' with three parameters
// and block until the build completes
job('example-2') {
    steps {
        remoteTrigger('test-ci', 'test-flow') {
            parameter('VERSION', '$PIPELINE_VERSION')
            parameters(BRANCH: 'feature-A', STAGING_REPO_ID: '41234232')
            blockBuildUntilComplete()
        }
    }
}
