job('example') {
    steps {
        jython('print "Hello" + "Goodbye"')
        jython(readFileFromWorkspace('build.py'))
    }
}
