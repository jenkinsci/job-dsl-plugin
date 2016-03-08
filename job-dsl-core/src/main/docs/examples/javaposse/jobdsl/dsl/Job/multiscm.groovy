job('example') {
    multiscm {
        git {
            remote {
                github('jenkinsci/jenkins')
            }
            extensions {
                relativeTargetDirectory('jenkins')
            }
        }
        git {
            remote {
                github('jenkinsci/job-dsl-plugin')
            }
            extensions {
                relativeTargetDirectory('job-dsl-plugin')
            }
        }
    }
}
