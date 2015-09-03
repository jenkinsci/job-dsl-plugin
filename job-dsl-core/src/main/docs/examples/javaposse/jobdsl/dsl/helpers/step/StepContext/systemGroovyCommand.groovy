job('example') {
    steps {
        systemGroovyCommand(readFileFromWorkspace('disconnect-slave.groovy')) {
            binding('computerName', 'ubuntu-04')
        }
    }
}
