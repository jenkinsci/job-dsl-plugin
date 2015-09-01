job('example') {
    wrappers {
        preScmSteps {
            steps {
                shell('echo Hello World')
            }
            failOnError()
        }
    }
}
