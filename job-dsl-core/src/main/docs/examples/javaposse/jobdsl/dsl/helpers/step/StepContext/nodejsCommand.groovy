job('example') {
    steps {
        nodejsCommand('console.log("Hello World!")', 'Node 0.12.0')
    }
}
