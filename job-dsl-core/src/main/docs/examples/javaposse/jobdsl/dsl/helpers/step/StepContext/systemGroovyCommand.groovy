job('example-1') {
    steps {
        systemGroovyCommand(readFileFromWorkspace('disconnect-slave.groovy')) {
            binding('computerName', 'ubuntu-04')
        }
    }
}

job('example-2') {
    steps {
        systemGroovyCommand {
            script("println 'Hello World!'")
            binding('computerName', 'ubuntu-04')
            classpath('file:/path/to/jar')
            classpath('http://url/to/jar')
            sandbox()
        }
    }
}
