pipelineJob('example') {
    definition {
        cpsScm {
            scm {
                git('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
        }
    }
}
