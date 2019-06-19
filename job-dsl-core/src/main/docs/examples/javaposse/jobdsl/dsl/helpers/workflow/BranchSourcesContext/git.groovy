multibranchPipelineJob('example') {
    branchSources {
        git {
            id('12121212') // IMPORTANT: use a constant and unique identifier
            remote('https://github.com/jenkinsci/job-dsl-plugin.git')
            credentialsId('github-ci')
            includes('JENKINS-*')
        }
    }
}
