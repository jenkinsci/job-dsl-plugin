job('example') {
    steps {
        systemGroovyScriptFile('disconnect-slave.groovy') {
            binding('computerName', 'ubuntu-04')
        }
    }
}
