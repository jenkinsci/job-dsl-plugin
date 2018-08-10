multibranchPipelineJob('example') {
    branchSources {
        svn {
            remoteBase('https://svn-server/repo/branches')
            credentialsId('svn-credentials')
        }
    }
}
