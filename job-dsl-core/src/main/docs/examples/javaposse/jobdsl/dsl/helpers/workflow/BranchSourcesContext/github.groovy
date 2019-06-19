multibranchPipelineJob('example') {
    branchSources {
        github {
            id('23232323') // IMPORTANT: use a constant and unique identifier
            scanCredentialsId('github-ci')
            repoOwner('OwnerName')
            repository('job-dsl-plugin')
        }
    }
}
