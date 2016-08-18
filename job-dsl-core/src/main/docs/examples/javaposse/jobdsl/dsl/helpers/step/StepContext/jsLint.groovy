job('example') {
    steps {
        jsLint {
            includePattern('**/*.js')
            excludePattern('**/*Tests.js')
            logFile('target/jslint.xml')
            arguments('-Dadsafe=true, -Dcontinue=true')
        }
    }
}
