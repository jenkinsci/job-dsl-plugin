job('example') {
    steps {
        shell('echo Hello World!')
        shell(readFileFromWorkspace('build.sh'))
    }
}
