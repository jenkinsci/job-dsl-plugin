job('example') {
    steps {
        python {
            command('python setup.py')
        }
    }
}
