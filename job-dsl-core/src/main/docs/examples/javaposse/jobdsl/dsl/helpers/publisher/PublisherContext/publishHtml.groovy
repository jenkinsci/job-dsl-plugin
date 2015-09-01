job('example') {
    publishers {
        publishHtml {
            report('build/test-output') {
                reportName('Test Output')
            }
            report('test') {
                reportName('Gradle Tests')
                keepAll()
                allowMissing()
                alwaysLinkToLastBuild()
            }
        }
    }
}
