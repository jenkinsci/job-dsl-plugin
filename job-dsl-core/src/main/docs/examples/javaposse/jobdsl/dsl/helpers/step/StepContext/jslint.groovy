job('example') {
    steps {
        jslint {
            includePattern('**/*.js')
            excludePattern('**/*Tests.js')
            logFile('target/jslint.xml')
            arguments('-Dadsafe=true, -Dcontinue=true')
        }
    }
}
