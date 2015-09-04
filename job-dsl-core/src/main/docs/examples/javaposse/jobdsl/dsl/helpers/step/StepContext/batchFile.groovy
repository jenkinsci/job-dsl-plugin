job('example') {
    steps {
        batchFile('echo Hello World!')
        batchFile(readFileFromWorkspace('build.bat'))
    }
}
