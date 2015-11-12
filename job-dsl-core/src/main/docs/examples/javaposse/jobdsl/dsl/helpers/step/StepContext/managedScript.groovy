job('example') {
    steps {
        managedScript('my-script') {
            arguments('World')
        }
    }
}
