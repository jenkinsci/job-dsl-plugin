multibranchWorkflowJob('example') {
    branchSources {
        github {
            scanCredentialsId('github-ci')
            repoOwner('OwnerName')
            repository('job-dsl-plugin')
        }
    }
}
