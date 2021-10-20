job('example') {
    steps {
        systemGroovyCommand(readFileFromWorkspace('disconnect_slave.groovy')) {
            binding('computerName', 'ubuntu-04')
        }
    }
}
