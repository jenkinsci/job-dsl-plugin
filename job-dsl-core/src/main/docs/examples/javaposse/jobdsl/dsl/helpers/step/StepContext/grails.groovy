job('example') {
    steps {
        grails {
            target('clean build')
            useWrapper(true)
        }
    }
}