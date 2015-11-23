workflowMultiBranchJob('example') {
    branchSource {
        git {
            id('exampleId')
            remote('')
            credentialsId('')
            includes('')
            excludes('')
            ignoreOnPushNotifications(false)
        }
    }
}
