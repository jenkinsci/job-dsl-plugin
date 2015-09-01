job('example') {
    steps {
        ant {
            target('test')
            targets(['publish', 'deploy'])
            prop('logging', 'info')
            props('test.threads': 10, 'input.status': 'release')
            buildFile('dir1/build.xml')
            javaOpt('-Xmx1g')
            javaOpts(['-Dprop2=value2', '-Dprop3=value3'])
            antInstallation('Ant 1.8')
        }
    }
}
