job('example') {
    parameters {
        booleanParam('RUN_TESTS', true, 'uncheck to disable tests')
    }
}