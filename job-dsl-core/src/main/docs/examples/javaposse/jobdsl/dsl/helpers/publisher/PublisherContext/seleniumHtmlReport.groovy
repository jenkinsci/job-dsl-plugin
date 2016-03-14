job('example') {
    publishers {
        seleniumHtmlReport('target/test-output') {
            failOnExceptions()
        }
    }
}
