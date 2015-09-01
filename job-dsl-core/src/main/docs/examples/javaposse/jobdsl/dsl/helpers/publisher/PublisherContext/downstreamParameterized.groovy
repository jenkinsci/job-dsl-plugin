job('example') {
    publishers {
        downstreamParameterized {
            trigger('Project1, Project2') {
                condition('UNSTABLE_OR_BETTER')
                parameters {
                    currentBuild()
                }
            }
        }
    }
}
