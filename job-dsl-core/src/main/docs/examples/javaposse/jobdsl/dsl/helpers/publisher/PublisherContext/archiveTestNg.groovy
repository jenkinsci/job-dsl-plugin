job('example-1') {
    publishers {
        archiveTestNG()
    }
}

job('example-2') {
    publishers {
        archiveTestNG('**/target/test-reports/*.xml')
    }
}

job('example-3') {
    publishers {
        archiveTestNG('**/target/test-reports/*.xml') {
            escapeTestDescription true
            escapeExceptionMessages false
            showFailedBuildsInTrendGraph true
            markBuildAsUnstableOnSkippedTests false
            markBuildAsFailureOnFailedConfiguration true
        }
    }
}
