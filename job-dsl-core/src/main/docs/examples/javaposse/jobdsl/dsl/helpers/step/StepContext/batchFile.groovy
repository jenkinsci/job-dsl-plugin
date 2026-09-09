job('example') {
    steps {
        batchFile('echo Hello World!')
        batchFile(readFileFromWorkspace('build.bat'))
    }
}

// mark the build as unstable instead of failed when the script exits with error level 3
job('example-2') {
    steps {
        batchFile('run-tests.bat') {
            unstableReturn(3)
        }
    }
}
