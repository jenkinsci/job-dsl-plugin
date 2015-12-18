job('example') {
    steps {
        nant {
            target('test')
            targets(['publish', 'deploy'])
            prop('logging', 'info')
            props('test.threads': 10, 'input.status': 'release')
            buildFile('dir1/build.xml')
            nantInstallation('NAnt 1.8')
        }
    }
}
