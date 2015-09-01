job('example') {
    publishers {
        violations(50) {
            sourcePathPattern('source pattern')
            fauxProjectPath('faux path')
            perFileDisplayLimit(51)
            checkstyle(10, 11, 10, 'test-report/*.xml')
            findbugs(12, 13, 12)
            jshint(10, 11, 10, 'test-report/*.xml')
        }
    }
}
