job('example-1') {
    publishers {
        buildPipelineTrigger('deploy-cluster-1, deploy-cluster-2')
    }
}

job('example-2') {
    publishers {
        buildPipelineTrigger('deploy-cluster-1, deploy-cluster-2') {
            parameters {
                predefinedProp('GIT_COMMIT', '$GIT_COMMIT')
                predefinedProp('ARTIFACT_BUILD_NUMBER', '$BUILD_NUMBER')
            }
        }
    }
}
