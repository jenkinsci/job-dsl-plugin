job('example') {
    steps {
        groovyScriptFile('generateReports.groovy') {
            groovyInstallation('groovy-2.4.2')
            scriptParam('target/reports')
        }
    }
}
