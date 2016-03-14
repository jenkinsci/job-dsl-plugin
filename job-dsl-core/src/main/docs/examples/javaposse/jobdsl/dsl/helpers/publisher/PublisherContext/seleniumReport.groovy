job('example') {
    publishers {
        seleniumReport('myproject/target/test-reports/*.html') {
            useTestCommands()
        }
    }
}
