ivyJob('example') {
    ivyBuilder {
        ant {
            target('clean')
            targets(['test', 'publish'])
            buildFile('build.xml')
            antInstallation('Ant 1.9')
            prop('key', 'value')
            javaOpt('-Xmx=1G')
        }
    }
}
