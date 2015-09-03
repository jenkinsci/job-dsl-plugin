job('example') {
    steps {
        groovyCommand(readFileFromWorkspace('generateReports.groovy')) {
            groovyInstallation('groovy-2.4.2')
            scriptParam('target/reports')
        }
    }
}
