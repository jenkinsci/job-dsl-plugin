job('example') {
    publishers {
        postBuildScripts {
            steps {
                shell('echo Hello World')
            }
            onlyIfBuildSucceeds(false)
            onlyIfBuildFails(false)
            markBuildUnstable(false)
        }
    }
}
