multibranchPipelineJob('example') {
    branchSources {
        gitlab {
            id('23232323') // IMPORTANT: use a constant and unique identifier
            serverName('GitLab')
            credentialsId('gitlab-ci')
            projectOwner('ownerName')
            projectPath('ownerName/projectName')
        }
    }
}
