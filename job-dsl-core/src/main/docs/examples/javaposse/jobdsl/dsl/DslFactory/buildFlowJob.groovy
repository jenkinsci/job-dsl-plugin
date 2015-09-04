// triple-quotes can be used for inline flow DSL definition
buildFlowJob('example-1') {
    buildFlow('''
        build("job1")
    ''')
}

// the build flow text can also be read from a file
buildFlowJob('example-2') {
    buildFlow(readFileFromWorkspace('my-build-flow-text.groovy'))
}
