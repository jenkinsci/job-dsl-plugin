job('example-1') {
    publishers {
        archiveArtifacts('build/test-output/**/*.html')
    }
}

job('example-2') {
    publishers {
        archiveArtifacts {
            pattern('build/test-output/**/*.html')
            pattern('build/test-output/**/*.xml')
            onlyIfSuccessful()
        }
    }
}
