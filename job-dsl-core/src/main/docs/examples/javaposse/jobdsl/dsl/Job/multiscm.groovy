job('example') {
    multiscm {
        git {
            remote {
                github('jenkinsci/jenkins')
            }
            relativeTargetDir('jenkins')
        }
        git {
            remote {
                github('jenkinsci/job-dsl-plugin')
            }
            relativeTargetDir('job-dsl-plugin')
        }
    }
}
