job('example') {
    steps {
        downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    predefinedProp('key1', 'value1')
                    predefinedProps([key2: 'value2', key3: 'value3'])
                }
            }
            trigger('Project2') {
                parameters {
                    currentBuild()
                }
            }
        }
    }
}
