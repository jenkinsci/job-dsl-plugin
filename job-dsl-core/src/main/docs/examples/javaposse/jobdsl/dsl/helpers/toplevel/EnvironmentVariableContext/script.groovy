job('example') {
    environmentVariables {
        script('''
           mkdir -p tests
           rm -rf /tmp/tests.tmp
        ''')
    }
}
