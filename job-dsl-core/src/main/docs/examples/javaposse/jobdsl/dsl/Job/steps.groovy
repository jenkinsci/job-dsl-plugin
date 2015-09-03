job('example') {
    steps {
        shell('echo START')
        gradle('check')
    }
}
