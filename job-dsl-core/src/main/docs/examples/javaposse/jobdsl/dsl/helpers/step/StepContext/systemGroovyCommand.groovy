job('example') {
    steps {
        systemGroovyCommand(readFileFromWorkspace('disconnect-slave.groovy')) {
            binding('computerName', 'ubuntu-04')
        }
    }
}

job('example') {
    steps {
        systemGroovyCommand() {
            script("println 'Hello World!'")
            binding('computerName', 'ubuntu-04')
            classpath('com.acme.example.jar')
            sandbox()
        }
    }
}
