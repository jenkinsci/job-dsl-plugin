job('example') {
    steps {
        systemGroovyCommand('disconnect-slave.groovy') {
            binding('computerName', 'ubuntu-04')
        }
    }
}
