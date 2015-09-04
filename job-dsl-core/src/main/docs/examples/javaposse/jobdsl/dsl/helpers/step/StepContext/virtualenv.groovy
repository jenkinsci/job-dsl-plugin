job('example') {
    steps {
        virtualenv {
            name('venv')
            command('pip install tox')
            clear()
        }
    }
}
