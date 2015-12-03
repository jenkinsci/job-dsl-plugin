job('example') {
    publishers {
        seleniumHtml('target/test-output') {
            failOnExceptions()
        }
    }
}
